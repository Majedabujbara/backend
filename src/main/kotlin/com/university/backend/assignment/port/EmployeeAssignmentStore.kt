package com.university.backend.assignment.port


import com.university.backend.exceptions.UserNotFoundException
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

data class EmployeeRecord(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val cafeteriaId: Int?
)

data class CafeteriaRecord(
    val cafeteriaId: Int,
    val name: String,
    val phone: String
)

interface EmployeeAssignmentStore {
    fun findAllUsers(): List<EmployeeRecord>
    fun findUserById(userId: Long): EmployeeRecord?
    fun findCafeteriaById(cafeteriaId: Int): CafeteriaRecord?
    fun updateEmployeeCafeteria(employeeId: Long, newCafeteriaId: Int): EmployeeRecord
}



class TempMockEmployeeAssignmentStore: EmployeeAssignmentStore {
    private val cafeterias = ConcurrentHashMap<Int, CafeteriaRecord>().apply {
        put(1, CafeteriaRecord(1, "Engineering Cafeteria", "065355001"))
        put(2, CafeteriaRecord(2, "Main Campus Cafeteria", "065355002"))
    }
    private val users = ConcurrentHashMap<Long, EmployeeRecord>().apply {
        put(1L, EmployeeRecord(1L, "Admin Account", "admin@uni.edu", "ADMIN", null))
        put(2L, EmployeeRecord(2L, "Customer Account", "customer@uni.edu", "CUSTOMER", null))
        put(3L, EmployeeRecord(3L, "Tariq Employee", "tariq@uni.edu", "EMPLOYEE", 1))
        put(4L, EmployeeRecord(4L, "Noor Employee", "noor@uni.edu", "EMPLOYEE", 2))
    }

    override fun findAllUsers(): List<EmployeeRecord> = users.values.toList()

    override fun findUserById(userId: Long): EmployeeRecord? = users[userId]

    override fun findCafeteriaById(cafeteriaId: Int): CafeteriaRecord? = cafeterias[cafeteriaId]

    override fun updateEmployeeCafeteria(employeeId: Long, newCafeteriaId: Int): EmployeeRecord {
        val current = users[employeeId]
            ?: throw UserNotFoundException("Employee with id $employeeId not found")
        val updated = current.copy(cafeteriaId = newCafeteriaId)
        users[employeeId] = updated
        return updated
    }

}