package net.aabergs.networkmanager.web.auth

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthenticationWeb {

    @GetMapping("/login")
    fun login() : String {
        return "auth/login"
    }

//    @PostMapping("/login")
//    fun login(model: Model, username: String, password: String) : String {
//        model.addAttribute("error", "An error")
//
//        return "auth/login"
//    }
}