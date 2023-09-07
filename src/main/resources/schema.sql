DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR(1024) NOT NULL,
    requestor_id BIGINT REFERENCES users (id),
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1024) NOT NULL,
  is_available BOOLEAN,
  owner_id BIGINT,
  request_id BIGINT,
  CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id)  REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id)  REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  items_id BIGINT,
  users_id BIGINT,
  status VARCHAR(64) NOT NULL,
  CONSTRAINT fk_bookings_to_users FOREIGN KEY(users_id) REFERENCES users(id),
  CONSTRAINT fk_bookings_to_items FOREIGN KEY(items_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  text VARCHAR(1024) NOT NULL,
  items_id BIGINT NOT NULL REFERENCES items (id),
  users_id BIGINT NOT NULL REFERENCES users (id),
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_comments_to_users FOREIGN KEY(users_id) REFERENCES users(id),
  CONSTRAINT fk_comments_to_items FOREIGN KEY(items_id) REFERENCES items(id)
);


