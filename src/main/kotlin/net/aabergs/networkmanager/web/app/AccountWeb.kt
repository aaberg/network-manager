package net.aabergs.networkmanager.web.app

import net.aabergs.networkmanager.bl.AccountManager
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView
import java.security.Principal

@Controller
@RequestMapping("/account")
class AccountWeb(private val accountManager: AccountManager) {

    @GetMapping("/register")
    fun createAccountPage(model: Model, principal: Principal) : String {
        model.addAttribute("user_id", principal.name)
        return "account/createAccount"
    }

    @PostMapping("/register")
    fun createAccount(principal: Principal, name: String, email: String) : RedirectView {
        println("name: $name, email: $email")

        accountManager.registerAccount(principal, name, email)

        return RedirectView("/app/dashboard")
    }
}