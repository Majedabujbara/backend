package com.university.backend.seed

import com.university.backend.cafeteria.Cafeteria
import com.university.backend.cafeteria.CafeteriaRepository
import com.university.backend.person.Person
import com.university.backend.person.PersonRepository
import com.university.backend.roles.Role
import com.university.backend.roles.RoleRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SeedDataInitializer(
    private val roleRepository: RoleRepository,
    private val personRepository: PersonRepository,
    private val cafeteriaRepository: CafeteriaRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private data class CafeteriaSeedData(
        val name: String,
        val phone: String
    )

    private data class EmployeeSeedData(
        val fullName: String,
        val phone: String,
        val email: String,
        val password: String,
        val cafeteriaName: String
    )

    @Transactional
    override fun run(vararg args: String) {
        seedRoles()
        val cafeteriasByName = seedCafeterias()
        seedAdminUser()
        seedEmployees(cafeteriasByName)
    }

    private fun seedRoles() {
        val requiredRoles = listOf("ADMIN", "EMPLOYEE", "CUSTOMER")

        requiredRoles.forEach { roleName ->
            if (roleRepository.findByRoleName(roleName) == null) {
                val role = Role().apply {
                    this.roleName = roleName
                }
                roleRepository.save(role)
            }
        }
    }

    private fun seedCafeterias(): Map<String, Cafeteria> {
        val seedList = listOf(
            CafeteriaSeedData(name = "Main Campus Cafeteria", phone = "065000000"),
            CafeteriaSeedData(name = "Engineering Cafeteria", phone = "065000002")
        )

        val resultMap = mutableMapOf<String, Cafeteria>()

        seedList.forEach { item ->
            val cafeteria = cafeteriaRepository.findByName(item.name)
                ?: cafeteriaRepository.save(
                    Cafeteria(
                        name = item.name,
                        phone = item.phone
                    )
                )
            resultMap[item.name] = cafeteria
        }

        return resultMap
    }

    private fun seedAdminUser() {
        val adminEmail = "admin.seed@admin.com"

        if (personRepository.findByEmail(adminEmail).isPresent) {
            return
        }

        val adminRole = roleRepository.findByRoleName("ADMIN")
            ?: throw IllegalStateException("Role ADMIN not found")

        val encodedPassword = passwordEncoder.encode("Admin@123")
            ?: throw IllegalStateException("Failed to encode admin password")

        val adminUser = Person().apply {
            fullName = "System Admin"
            phone = "065000001"
            email = adminEmail
            hashPassword = encodedPassword
            role = adminRole
            cafeteria = null
        }

        personRepository.save(adminUser)
    }

    private fun seedEmployees(cafeteriasByName: Map<String, Cafeteria>) {
        val employeeRole = roleRepository.findByRoleName("EMPLOYEE")
            ?: throw IllegalStateException("Role EMPLOYEE not found")

        val employeesSeedList = listOf(
            // موظفان في الكافتيريا الأولى (Main Campus Cafeteria)
            EmployeeSeedData(
                fullName = "Employee One Main",
                phone = "065000101",
                email = "emp1.main@emp.com",
                password = "Emp@12345",
                cafeteriaName = "Main Campus Cafeteria"
            ),
            EmployeeSeedData(
                fullName = "Employee Two Main",
                phone = "065000102",
                email = "emp2.main@emp.com",
                password = "Emp@12345",
                cafeteriaName = "Main Campus Cafeteria"
            ),
            // موظفان في الكافتيريا الثانية (Engineering Cafeteria)
            EmployeeSeedData(
                fullName = "Employee One Eng",
                phone = "065000201",
                email = "emp1.eng@emp.com",
                password = "Emp@12345",
                cafeteriaName = "Engineering Cafeteria"
            ),
            EmployeeSeedData(
                fullName = "Employee Two Eng",
                phone = "065000202",
                email = "emp2.eng@emp.com",
                password = "Emp@12345",
                cafeteriaName = "Engineering Cafeteria"
            )
        )

        employeesSeedList.forEach { emp ->
            if (personRepository.findByEmail(emp.email).isPresent) {
                return@forEach
            }

            val targetCafeteria = cafeteriasByName[emp.cafeteriaName]
                ?: throw IllegalStateException("Cafeteria '${emp.cafeteriaName}' not found")

            val encodedPassword = passwordEncoder.encode(emp.password)
                ?: throw IllegalStateException("Failed to encode password for ${emp.email}")

            val employeeUser = Person().apply {
                fullName = emp.fullName
                phone = emp.phone
                email = emp.email
                hashPassword = encodedPassword
                role = employeeRole
                cafeteria = targetCafeteria
            }

            personRepository.save(employeeUser)
        }
    }
}