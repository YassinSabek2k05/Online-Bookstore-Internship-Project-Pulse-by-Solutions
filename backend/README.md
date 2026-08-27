```txt
com.bookstore
├── config/
│   ├── SecurityConfig.java       // Spring Security + JWT filter chain
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java       // /api/auth/register, /login
│   ├── UserController.java       // /api/users/me
│   ├── AdminController.java      // /api/admins (CRUD)
│   └── BookController.java       // /api/books (CRUD)
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── AdminService.java
│   └── BookService.java
├── repository/
│   ├── UserRepository.java
│   └── BookRepository.java
├── entity/
│   ├── User.java
│   └── Book.java
├── dto/
│   ├── request/   (RegisterRequest, LoginRequest, BookRequest...)
│   └── response/  (UserResponse, BookResponse, JwtResponse...)
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── UserDetailsServiceImpl.java
├── exception/
│   ├── GlobalExceptionHandler.java   // @RestControllerAdvice
│   ├── ResourceNotFoundException.java
│   └── DuplicateResourceException.java
└── enums/
└── Role.java   // USER, ADMIN
```
