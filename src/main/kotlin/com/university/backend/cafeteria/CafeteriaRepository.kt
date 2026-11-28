package com.university.backend.cafeteria

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CafeteriaRepository : JpaRepository<Cafeteria, Int> {
    fun findByName(name: String): Cafeteria?
}