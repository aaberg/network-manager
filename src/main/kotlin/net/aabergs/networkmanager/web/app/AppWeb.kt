package net.aabergs.networkmanager.web.app

import net.aabergs.networkmanager.bl.AccountManager
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal

@Controller
@RequestMapping("/app")
class AppWeb(
    private val accountManager: AccountManager
) {

    @GetMapping("/dashboard")
    fun dashboard(model: Model, principal: Principal) : String {
        model.addAttribute("account", accountManager.getAccount(principal))
        return "app/dashboard"
    }

    @GetMapping("/contacts")
    fun contacts(model: Model, principal: Principal) : String {
        model.addAttribute("account", accountManager.getAccount(principal))
        return "app/contacts"
    }

    @GetMapping("/contacts/new")
    fun newContact(model: Model, principal: Principal) : String {
        model.addAttribute("account", accountManager.getAccount(principal))
        return "app/newContact"
    }

    data class NewContactRequest(val name: String, val email: String, val phone: String)
    @PostMapping("/contacts/new")
    fun createNewContact(newContactRequest: NewContactRequest) {
        throw Exception("Not implemented")
    }
}