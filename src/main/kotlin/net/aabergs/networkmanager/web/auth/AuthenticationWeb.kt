package net.aabergs.networkmanager.web.auth

import net.aabergs.networkmanager.bl.AccountManager
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView
import java.security.Principal

@Controller
@RequestMapping("/auth")
class AuthenticationWeb(
    private val accountManager: AccountManager) {

    @GetMapping("/login")
    fun login() : String {
        return "auth/login"
    }

    @GetMapping("/postlogin")
    fun postLogin(principal: Principal) : RedirectView {
        accountManager.getAccount(principal) ?: return RedirectView("/account/register")

        return RedirectView("/app/dashboard")
    }
}