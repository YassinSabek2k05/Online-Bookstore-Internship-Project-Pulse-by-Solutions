/**
 * The backend stores category as a free String, so this is the frontend's
 * source of truth for what an admin may pick. Grouped because the flat list
 * got long enough to be awkward to scan in a dropdown.
 */
export const BOOK_CATEGORY_GROUPS = [
  {
    label: 'Literature',
    categories: [
      'Classic',
      'Fiction',
      'Non-fiction',
      'Mystery & Thriller',
      'Science Fiction',
      'Fantasy',
      'Romance',
      'Horror',
      'History',
      'Biography',
      'Poetry',
      "Children's",
    ],
  },
  {
    label: 'Computer Science',
    categories: [
      'Computer Science',
      'Programming',
      'Software Engineering',
      'Algorithms & Data Structures',
      'Artificial Intelligence',
      'Machine Learning',
      'Data Science',
      'Databases',
      'Networking',
      'Cybersecurity',
      'Web Development',
      'Operating Systems',
    ],
  },
] as const;

/** Flat view, for checking whether a stored value is still a known category. */
export const BOOK_CATEGORIES: readonly string[] = BOOK_CATEGORY_GROUPS.flatMap(
  (group) => group.categories,
);

export interface Book {
  id: number;
  title: string;
  author: string;
  category: string;
  price: number;
  /** Optional on the backend — BookRequest has no @NotBlank on these two. */
  description: string | null;
  imageUrl: string | null;
}

export type BookRequest = Omit<Book, 'id'>;
