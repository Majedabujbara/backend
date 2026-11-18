package com.university.backend.service

import com.university.backend.dto.LoginRequest
import org.springframework.stereotype.Service

@Service
class AuthService {

    fun authenticateUser(request: LoginRequest): String {
        // حالياً هون رح نحط منطق التحقق من الإيميل والباسورد
        // مستقبلاً رح نربطه بقاعدة البيانات ونجيب التوكن من زميلي
        return "User authenticated successfully for: ${request.email}"
    }
}