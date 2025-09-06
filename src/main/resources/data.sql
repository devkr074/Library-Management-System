INSERT INTO publishers (publisher_id, name, address, contact) VALUES (1, 'O\' Reilly Media', '1005 Gravenstein Hwy N, Sebastopol', 'support@oreilly.com');
INSERT INTO publishers (publisher_id, name, address, contact) VALUES (2, 'Penguin Random House', '1745 Broadway, New York', 'info@penguinrandomhouse.com');

INSERT INTO categories (category_id, name, description) VALUES (1, 'Programming', 'Software development books');
INSERT INTO categories (category_id, name, description) VALUES (2, 'Fiction', 'Novels and stories');

INSERT INTO authors (author_id, first_name, last_name, bio) VALUES (1, 'Robert', 'Martin', 'Author of Clean Code');
INSERT INTO authors (author_id, first_name, last_name, bio) VALUES (2, 'Martin', 'Fowler', 'Software architect');

INSERT INTO books (book_id, title, isbn, publisher_id, published_date, pages, description, category_id) VALUES
  (1, 'Clean Code', '9780132350884', 1, '2008-08-11', 464, 'A Handbook of Agile Software Craftsmanship', 1),
  (2, 'Refactoring', '9780201485677', 2, '1999-07-08', 448, 'Improving the Design of Existing Code', 1);

INSERT INTO book_author (book_id, author_id) VALUES (1, 1);
INSERT INTO book_author (book_id, author_id) VALUES (2, 2);

INSERT INTO book_copies (copy_id, book_id, barcode, acquired_date, status, shelf_location) VALUES
  (1, 1, 'BC-0001', '2023-01-15', 'AVAILABLE', 'SHELF-A1'),
  (2, 1, 'BC-0002', '2023-01-16', 'AVAILABLE', 'SHELF-A1'),
  (3, 2, 'BC-0003', '2023-02-10', 'AVAILABLE', 'SHELF-A2');

INSERT INTO borrowers (borrower_id, first_name, last_name, email, phone, address, membership_date, active, max_allowed_loans) VALUES
  (1, 'Dev', 'Kumar', 'dev.kumar@example.com', '9876543210', 'Somewhere, India', '2024-06-01', 1, 5);