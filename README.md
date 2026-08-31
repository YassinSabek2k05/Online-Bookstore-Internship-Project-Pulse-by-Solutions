# Online Bookstore

Full-stack online bookstore built as a summer training project at Pulse by Solutions.
Customers browse a catalogue of books; administrators manage the catalogue and the
administrator accounts themselves.

**Stack:** Angular 22 · Spring Boot 4 · Java 21 · PostgreSQL · Spring Security + JWT

---

## Table of contents

- [Features](#features)
- [Architecture](#architecture)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Authentication model](#authentication-model)
- [Image storage — FakeImageManager](#image-storage--fakeimagemanager)
- [API reference](#api-reference)
- [Validation rules](#validation-rules)
- [Error handling](#error-handling)
- [Project structure](#project-structure)
- [Known limitations](#known-limitations)

---

## Features

### Visitor (not signed in)
- Landing page with the store's pitch and links to sign in / sign up
- Registration and login

### Customer (`USER`)
- Storefront with a grid of every book — cover, title, price
- Book details page — cover, title, author, category, price, full description
- Light / dark theme, remembered between visits

### Administrator (`ADMIN`)
- Everything a customer can do
- **Manage Books** — create, edit and delete books, including cover upload
- **Manage Admins** — list, create and remove administrator accounts

Signing in sends administrators to `/admin/books` and customers to `/home`.
Administrators can reach the dashboard from the storefront at any time via the
**Admin** link in the navigation bar.

---

## Architecture

```
Angular 22 (:4200)                    Spring Boot (:8080)                PostgreSQL
┌────────────────────┐                ┌───────────────────────┐          ┌──────────┐
│ components         │                │ Controller            │          │ users    │
│   ↓                │  HTTP + cookie │   ↓                   │          │ books    │
│ ApiService ────────┼───────────────▶│ Service               │          │ book_    │
│   ↓                │   (JWT)        │   ↓                   │◀────────▶│  image   │
│ route guards       │                │ Repository            │          └──────────┘
└────────────────────┘                └───────────────────────┘
```

The backend follows a strict **Controller → Service → Repository** layering; no
controller touches a repository directly, and every endpoint speaks in DTOs rather
than exposing JPA entities.

The frontend mirrors that idea: components never call `HttpClient` directly. A single
`ApiService` resolves paths against the API base URL and normalises every failure into
one `ApiError` shape, and thin per-resource services (`BooksApi`, `AdminsApi`,
`UsersApi`, `AuthApi`, `ImagesApi`) sit on top of it.

---

## Getting started

### Prerequisites

| Tool | Version |
|---|---|
| JDK | 21 |
| Node.js | 20+ |
| PostgreSQL | 14+ |

### 1. Database

```bash
createdb -U postgres postgres    # or use any database you like
```

Schema creation is automatic — `spring.jpa.hibernate.ddl-auto=update` builds the
tables on first run.

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Runs on **http://localhost:8080**.
API docs: **http://localhost:8080/swagger-ui.html**

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

Runs on **http://localhost:4200**.

### 4. Create the first administrator

Registration always produces a `USER`, by design — the role can never be set from a
request. Promote the first account by hand:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'you@example.com';
```

Every administrator after that can be created from the **Manage Admins** page.

---

## Configuration

**Backend** — `backend/src/main/resources/application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: 0000
jwt_cookie:
  value:
    staff_time: 86400000   # token lifetime in ms (24 hours)
```

**Frontend** — `frontend/src/environments/environment.ts`

```ts
const serverUrl = 'http://localhost:8080';
export const environment = { serverUrl, apiUrl: `${serverUrl}/api` };
```

`serverUrl` is kept separate from `apiUrl` because image paths come back
server-relative and need the origin prefixed before they can be used as an
`<img>` source.

---

## Authentication model

Login returns **no token in the response body**. Instead it sets an **httpOnly
cookie** named `user_token` holding the JWT:

```
POST /api/auth/login  ──▶  Set-Cookie: user_token=<jwt>; HttpOnly; Secure; SameSite=None
```

This has a few consequences worth understanding:

- **JavaScript cannot read the token.** That is the point — it removes an entire class
  of XSS token-theft attacks.
- **Every API request must send credentials.** A single Angular interceptor
  (`credentialsInterceptor`) adds `withCredentials: true` to any request aimed at the
  API, so no calling code has to remember.
- **The client cannot tell who is signed in on its own.** `GET /api/users/me` is the
  only way to resolve the cookie into a user, which is what the route guards call.
- **Logout must happen server-side.** `POST /api/auth/logout` re-sends the cookie with
  `Max-Age=0`. The delete cookie is built by the same helper as the login cookie,
  because a browser only replaces a cookie when the name, path and attributes all match.

Route guards (`authGuard`, `adminGuard`, `guestGuard`) are a **convenience, not a
security boundary** — the backend authorises every request independently, so a `USER`
calling an admin endpoint directly still gets a `403`.

---

## Image storage — FakeImageManager

Book covers are handled by `FakeImageManager`, which implements the
`ImageStorageService` interface. It stores uploaded images **directly in the database**
as bytes on the `BookImage` entity, rather than writing to disk or a cloud bucket —
hence "Fake": it stands in for real object storage such as S3. Because it sits behind
an interface, a production implementation can be swapped in without touching a single
controller or service.

### Upload flow

The cover is uploaded **before** the book is saved, not as part of it:

```
1. Admin picks a file
        │
        ▼
2. POST /api/images        (multipart, field name "file", ADMIN only)
        │                  validates extension, generates <uuid>.<ext>,
        │                  stores bytes + content type
        ▼
3. Returns "/api/images/<key>"   ← plain text, not JSON
        │
        ▼
4. Held in the form, then submitted as the book's imageUrl
        │
        ▼
5. POST /api/books { ..., "imageUrl": "/api/images/<key>" }
```

Accepted extensions: `.jpg`, `.jpeg`, `.png`, `.webp` — checked both client-side and
in `FakeImageManager.getExtension()`.

### Serving and cleanup

`GET /api/images/{key}` streams the bytes back with their stored content type and is
**public**, so `<img>` tags render without a token. `DELETE /api/images/{key}` is
`ADMIN` only.

Because the upload happens first, abandoning the form would otherwise leave an orphaned
image. The admin dialog tracks only the images uploaded during that session and deletes
them on cancel or replace — a book's pre-existing cover is never in that set, so backing
out of an edit can never destroy a saved cover.

### Notes

- `imageUrl` is stored **server-relative**. The frontend's `resolveImageUrl()` prefixes
  the origin, and passes absolute `http(s)` URLs through untouched — so a seed file can
  reference external cover images and they will render.
- `imageUrl` is optional. Books without one fall back to a generated typographic cover,
  coloured deterministically from the book's id.

---

## API reference

All paths are prefixed with `/api`.

### Auth — public

| Method | Path | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new account (always `USER`) |
| `POST` | `/auth/login` | Authenticate, sets the `user_token` cookie |
| `POST` | `/auth/logout` | Expires the cookie |

### Users

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/users/me` | `USER` `ADMIN` | The signed-in user |

### Books

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/books` | `USER` `ADMIN` | All books |
| `GET` | `/books/{id}` | `USER` `ADMIN` | One book |
| `POST` | `/books` | `ADMIN` | Create |
| `PUT` | `/books/{id}` | `ADMIN` | Update |
| `DELETE` | `/books/{id}` | `ADMIN` | Delete |

### Admins

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/admins` | `ADMIN` | All administrators |
| `POST` | `/admins` | `ADMIN` | Create an administrator |
| `DELETE` | `/admins/{id}` | `ADMIN` | Remove an administrator |

`POST /admins` takes the same `RegisterRequest` as public registration — the `ADMIN`
role is assigned server-side. The request has no `role` field at all, so privilege
escalation from a request body is structurally impossible. Administrators cannot delete
their own account, which prevents locking everyone out of the dashboard.

### Images

| Method | Path | Access | Description |
|---|---|---|---|
| `POST` | `/images` | `ADMIN` | Upload, returns the path as plain text |
| `GET` | `/images/{key}` | Public | Stream the image |
| `DELETE` | `/images/{key}` | `ADMIN` | Delete |

---

## Validation rules

Enforced by Jakarta Bean Validation on the backend and mirrored in the Angular forms.
The backend is authoritative; the frontend copies exist only to give faster feedback.

| Field | Rule |
|---|---|
| Email | Valid format, unique |
| Phone | `^\+?[0-9]{10,15}$` — digits only, optional leading `+` |
| Password | At least 8 characters |
| Confirm password | Must match |
| Book title / author / category | Not blank |
| Book price | Greater than zero |

---

## Error handling

`GlobalExceptionHandler` (`@RestControllerAdvice`) returns a consistent body for every
failure:

```json
{ "status": 404, "message": "Book not found with id: 12" }
```

| Status | Cause |
|---|---|
| `400` | Validation failure or invalid request |
| `401` | Missing or invalid token |
| `403` | Authenticated but not permitted |
| `404` | Resource does not exist |
| `409` | Duplicate resource, e.g. an email already registered |

`401` and `403` are produced by Spring Security's entry point and access-denied handler
in the same shape, so the frontend only ever parses one error format.

---

## Project structure

```
backend/src/main/java/com/pulsebysolutions/onlinebookstoreinternshipproject/
├── config/          SecurityConfig, AppConfig, OpenApiConfig
├── controller/      Auth, User, Book, Admin, ImageStorage
├── dto/
│   ├── request/     LoginRequest, RegisterRequest, BookRequest
│   └── response/    UserResponse, BookResponse
├── entity/          User, Book, BookImage
├── exception/       GlobalExceptionHandler + custom exceptions
├── interfaces/      ImageStorageService
├── repository/      Spring Data JPA repositories
├── security/        JWTService, JWTFilter
└── service/         AuthService, BookService, AdminService,
                     UserService, FakeImageManager

frontend/src/app/
├── core/
│   ├── guards/      authGuard, adminGuard, guestGuard
│   ├── interceptors/ credentialsInterceptor
│   ├── models/      User, Book, ApiError
│   ├── services/    ApiService, AuthService, ThemeService, api/*
│   └── utils/       resolveImageUrl
├── features/
│   ├── admin/       admin-layout, manage-books, manage-admins
│   ├── auth/        login, signup
│   ├── customer/    home, book-details
│   └── landing/
└── shared/          navbar, footer, book-cover, theme-toggle, confirm-dialog
```

---

## Known limitations

- **No pagination.** `GET /api/books` returns every row. Fine at this scale; a
  `Pageable` parameter is where it would go.
- **Images live in the database.** Convenient for a training project, not what you would
  ship — see [FakeImageManager](#image-storage--fakeimagemanager).
- **The JWT secret is committed** in `JWTService`. It belongs in an environment variable
  for anything real.
- **The first administrator must be promoted with SQL**, since registration always
  produces a `USER`.
