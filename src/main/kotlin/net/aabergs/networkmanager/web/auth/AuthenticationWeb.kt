package net.aabergs.networkmanager.web.auth

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthenticationWeb {

    @GetMapping("/login")
    fun login() : String {
        return "auth/login"
    }
}