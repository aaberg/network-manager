package net.aabergs.networkmanager.web.app

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/app")
class AppWeb {

    @GetMapping("/dashboard")
    fun dashboard() : String {
        return "app/dashboard"
    }

    @GetMapping("/contacts")
    fun contacts() : String {
        return "app/contacts"
    }
}