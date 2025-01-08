package net.aabergs.networkmanager.web.app

import net.aabergs.networkmanager.bl.AccountManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView
import java.security.Principal

@Controller
@RequestMapping("/app")
class RedirectWeb(val accountManager: AccountManager) {

    @GetMapping("/dashboard")
    fun dashboard(principal: Principal) : RedirectView {
        val account = accountManager.getAccount(principal) ?: return RedirectView("/account/register")
        val tenant = account.tenants.first()

        return RedirectView("/app/${tenant.id}/dashboard")
    }
}