CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY NOT NULL,
  uuid UUID UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR NOT NULL,
  activated BOOLEAN NOT NULL,
  terms_confirmed BOOLEAN NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz
);
