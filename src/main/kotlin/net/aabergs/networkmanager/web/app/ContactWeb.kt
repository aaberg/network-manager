package net.aabergs.networkmanager.web.app

import net.aabergs.networkmanager.bl.AccountManager
import net.aabergs.networkmanager.bl.AggregateManager
import net.aabergs.networkmanager.bl.contact.ContactAggregate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal
import java.util.UUID

@Controller
@RequestMapping("/app/{tenantId}/contacts/{contactId}")
class ContactWeb(
    private val aggregateManager: AggregateManager,
    private val accountManager: AccountManager
) {

    @GetMapping
    fun contact(
        model: Model,
        principal: Principal,
        @PathVariable("tenantId") tenantId: Long,
        @PathVariable("contactId") contactId: UUID
    ): String {
        val tenant = accountManager.validateAccessAndGetTenant(principal, tenantId)
        val contact = aggregateManager.loadState(contactId) {ContactAggregate()}

        model.addAttribute("tenant", tenant)
        model.addAttribute("contact", contact)

        return "contact/contactView"
    }
}