package com.university.backend.exceptions

class InvalidEmployeeAssignmentException (
    message : String = "Invalid Employee Assignment"
): RuntimeException(message)