package com.university.backend.cafeteria

import com.university.backend.cafeteria.Cafeteria
import org.springframework.data.jpa.repository.JpaRepository

interface cafeRepository : JpaRepository<Cafeteria, Int>