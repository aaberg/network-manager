package net.aabergs.networkmanager.web.open

import net.aabergs.networkmanager.web.CookieSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class OpenWeb(private val cookieSession: CookieSession) {

    @GetMapping("/")
    fun index(model: Model) : String {
        model.addAttribute("tenantId", cookieSession.tenantId)
        return "index"
    }
}