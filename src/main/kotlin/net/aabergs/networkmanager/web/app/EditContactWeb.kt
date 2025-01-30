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

    @GetMapping()
    fun editContact(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        model.addAttribute("contact", aggregateManager.loadState<ContactAggregate>(contactId){ ContactAggregate() })

        return "editContact/editContact"
    }

    @GetMapping("emails")
    fun emailView(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        model.addAttribute("contact", aggregateManager.loadState<ContactAggregate>(contactId) { ContactAggregate() })

        return "editContact/emailView"
    }

    @GetMapping("/add-email")
    fun getAddEmailView(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
        model.addAttribute("contact", aggregateManager.loadState<ContactAggregate>(contactId) { ContactAggregate() })

        return "editContact/addEmail"
    }

    @PostMapping("/add-email")
    fun addEmail(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long,
                 @PathVariable("contactId") contactId: UUID, email: String, isPrimary: Boolean) : String {
        val contact = aggregateManager.loadState(contactId) { ContactAggregate() }
        val emailObj = Email(UUID.randomUUID(), email)

        contact.addEmail(emailObj)
        if (isPrimary) {
            contact.primaryEmail = emailObj
        }
        aggregateManager.saveState(contact)

        return emailView(model, principal, tenantId, contactId)
    }


//    @PostMapping("/email")
//    fun addEmail(model: Model, principal: Principal, @PathVariable("tenantId") tenantId: Long, @PathVariable("contactId") contactId: UUID) : String {
//        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
//        val contact = aggregateManager.loadState(contactId) { ContactAggregate() }
//        contact.addEmail(Email(UUID.randomUUID(),""))
//
//        aggregateManager.saveState(contact)
//        model.addAttribute("contact", contact)
//
//        return "editContact/editContact"
//    }

//    @PutMapping("/email/{emailId}")
//    fun updateEmail(
//        model: Model,
//        principal: Principal,
//        @PathVariable("tenantId") tenantId: Long,
//        @PathVariable("contactId") contactId: UUID,
//        @PathVariable("emailId") emailId: UUID,
//        email: String)
//            : String
//    {
//        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
//        val contact = aggregateManager.loadState(contactId) { ContactAggregate() }
//        contact.updateEmail(Email(emailId, email))
//
//        aggregateManager.saveState(contact)
//        model.addAttribute("contact", contact)
//
//        return "editContact/editContact"
//    }
//
//    @DeleteMapping("/email/{emailId}")
//    fun removeEmail(
//        model: Model,
//        principal: Principal,
//        @PathVariable("tenantId") tenantId: Long,
//        @PathVariable("contactId") contactId: UUID,
//        @PathVariable("emailId") emailId: UUID
//    )
//            : String {
//        model.addAttribute("tenant", accountManager.validateAccessAndGetTenant(principal, tenantId))
//
//        val contact = aggregateManager.loadState(contactId) { ContactAggregate() }
//
//        contact.removeEmail(contact.emails.find { it.id == emailId } ?: throw IllegalArgumentException("Email not found"))
//        aggregateManager.saveState(contact)
//
//        model.addAttribute("contact", contact)
//
//        return "editContact/editContact"
//    }
}