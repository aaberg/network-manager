package net.aabergs.networkmanager.web.app

import net.aabergs.networkmanager.bl.AccountManager
import net.aabergs.networkmanager.bl.AggregateManager
import net.aabergs.networkmanager.bl.contact.ContactAggregate
import net.aabergs.networkmanager.bl.contact.Email
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*

@Controller
@RequestMapping("/app/{tenantId}/contacts/{contactId}")
class EditContactWeb(
    private val accountManager: AccountManager,
    private val aggregateManager: AggregateManager) {

    @GetMapping
    fun editContact(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        val contact = aggregateManager.loadState<ContactAggregate>(contactId){ ContactAggregate() }
        model.addAttribute("contact", contact)

        return "editContact/editContact"
    }

    @GetMapping("emails")
    fun emailView(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        model.addAttribute("contact", aggregateManager.loadState<ContactAggregate>(contactId) { ContactAggregate() })

        return "editContact/emailView"
    }

    @GetMapping("/add-email")
    fun getAddEmailView(model: Model,
                        principal: Principal,
                        @PathVariable("tenantId") tenantId: Long,
                        @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        model.addAttribute("contact", aggregateManager.loadState<ContactAggregate>(contactId) { ContactAggregate() })
        model.addAttribute("email", Email(UUID.randomUUID(), ""))
        model.addAttribute("isNew", true)

        return "editContact/setEmail"
    }

    @GetMapping("/update-email/{emailId}")
    fun updateEmailView(model: Model,
                        principal: Principal,
                        @PathVariable("tenantId") tenantId: Long,
                        @PathVariable("contactId") contactId: UUID,
                        @PathVariable("emailId") emailId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        val contact = aggregateManager.loadState<ContactAggregate>(contactId) { ContactAggregate() }
        model.addAttribute("contact", contact)

        val email = contact.emails.first { it.id == emailId }
        model.addAttribute("email", email)
        model.addAttribute("isNew", false)

        return "editContact/setEmail"
    }

    data class SetEmailForm(
        val id: UUID,
        val email: String,
        val isPrimary: Boolean = false
    )
    @PostMapping("/set-email")
    fun setEmail(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long,
                 @PathVariable("contactId") contactId: UUID,
                 emailForm: SetEmailForm) : String {
        val contact = aggregateManager.loadState(contactId) { ContactAggregate() }
        val email = Email(emailForm.id, emailForm.email)

        contact.setEmail(email)
        if (emailForm.isPrimary) {
            contact.primaryEmail = email
        }
        aggregateManager.saveState(contact)

        return emailView(model, principal, tenantId, contactId)
    }
}