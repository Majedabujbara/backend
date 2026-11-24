-- V2: Create sessions table linked to people

CREATE TABLE IF NOT EXISTS sessions (
                                        session_id BIGSERIAL PRIMARY KEY,

                                        person_id BIGINT NOT NULL,

                                        token VARCHAR(512) NOT NULL UNIQUE,

    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_sessions_person FOREIGN KEY (person_id) REFERENCES people (person_id)
    );