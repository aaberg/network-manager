package net.aabergs.networkmanager.web.app

import kotlinx.datetime.Clock
import net.aabergs.networkmanager.bl.AccountManager
import net.aabergs.networkmanager.bl.AggregateManager
import net.aabergs.networkmanager.bl.NewContactCreated
import net.aabergs.networkmanager.bl.contact.ContactAggregate
import net.aabergs.networkmanager.bl.contact.Email
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView
import java.lang.ProcessBuilder.Redirect
import java.security.Principal
import java.util.*

@Controller
@RequestMapping("/app/{tenantId}")
class AppWeb(
    private val accountManager: AccountManager,
    private val aggregateManager: AggregateManager
) {

    @GetMapping("/dashboard")
    fun dashboard(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long) : String {
        model.addAttribute("account", accountManager.getAccount(principal))
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        return "app/dashboard"
    }

    @GetMapping("/contacts")
    fun contacts(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long) : String {
        model.addAttribute("account", accountManager.getAccount(principal))
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        model.addAttribute("contacts", aggregateManager.getContactList(tenantId))
        return "app/contacts"
    }

    @GetMapping("/contacts/new")
    fun newContact(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long) : String {
        model.addAttribute("account", accountManager.getAccount(principal))
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        return "app/newContact"
    }

    data class NewContactRequest(val name: String)
    @PostMapping("/contacts/new")
    fun createNewContact(request: NewContactRequest, principal: Principal, @PathVariable("tenantId") tenantId: Long) : RedirectView {
        val tenant = accountManager.validateAccessAndGetTenant(principal, tenantId)
        val contact = ContactAggregate(UUID.randomUUID(), request.name, Clock.System.now(), tenant.id)
        aggregateManager.saveState(contact)

        return RedirectView("/app/$tenantId/contacts")
    }

    @GetMapping("/contacts/{contactId}")
    fun editContact(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        model.addAttribute("contact", aggregateManager.loadState<ContactAggregate>(contactId){ContactAggregate()})

        return "app/editContact"
    }

    @PostMapping("/contacts/{contactId}/email")
    fun addEmail(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        val contact = aggregateManager.loadState(contactId) {ContactAggregate()}
        contact.addEmail(Email(UUID.randomUUID(),""))

        aggregateManager.saveState(contact)
        model.addAttribute("contact", contact)

        return "app/editContact"
    }

    @PutMapping("/contacts/{contactId}/email/{emailId}")
    fun updateEmail(
        model: Model,
        principal: Principal,
        @PathVariable("tenantId") tenantId: Long,
        @PathVariable("contactId") contactId: UUID,
        @PathVariable("emailId") emailId: UUID,
        email: String)
    : String
    {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        val contact = aggregateManager.loadState(contactId) { ContactAggregate() }
        contact.updateEmail(Email(emailId, email))

        aggregateManager.saveState(contact)
        model.addAttribute("contact", contact)

        return "app/editContact"
    }

    @DeleteMapping("/contacts/{contactId}/email/{emailId}")
    fun removeEmail(
        model: Model,
        principal: Principal,
        @PathVariable("tenantId") tenantId: Long,
        @PathVariable("contactId") contactId: UUID,
        @PathVariable("emailId") emailId: UUID)
    : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))

        val contact = aggregateManager.loadState(contactId) { ContactAggregate() }

        contact.removeEmail(contact.emails.find { it.id == emailId } ?: throw IllegalArgumentException("Email not found"))
        aggregateManager.saveState(contact)

        model.addAttribute("contact", contact)

        return "app/editContact"
    }

//    @GetMapping("/contacts/{contactId}/emails")
//    fun editContactEmails(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
//        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
//        model.addAttribute("contact", aggregateManager.loadState<ContactAggregate>(contactId){ContactAggregate()})
//
//        return "app/editContactEmails"
//    }
}