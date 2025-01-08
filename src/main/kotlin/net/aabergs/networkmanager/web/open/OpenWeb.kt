package net.aabergs.networkmanager.web.open

import net.aabergs.networkmanager.web.CookieSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class OpenWeb(private val cookieSession: CookieSession) {

    @GetMapping("/")
    fun index() : String {
        return "index"
    }
}