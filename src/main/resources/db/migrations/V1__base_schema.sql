-- V1: Base schema reference for roles, cafeterias, and people

CREATE TABLE IF NOT EXISTS roles (
                                     role_id BIGSERIAL PRIMARY KEY,
                                     role_name VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS cafeterias (
                                          cafeteria_id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS people (
                                      person_id BIGSERIAL PRIMARY KEY,
                                      full_name VARCHAR(75) NOT NULL,
    last_name VARCHAR(75) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    hash_password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    cafeteria_id BIGINT,
    created_at TIMESTAMP(6) NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_people_role FOREIGN KEY (role_id) REFERENCES roles (role_id),
    CONSTRAINT fk_people_cafeteria FOREIGN KEY (cafeteria_id) REFERENCES cafeterias (cafeteria_id)
    );