package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Instant
import net.aabergs.networkmanager.bl.*
import java.util.*

class ContactAggregate() : Aggregate() {
    constructor(id: UUID, name: String, created: Instant, tenantId: Long) : this() {
        apply(NewContactCreated(id, name, created, tenantId))
    }

    var name = ""
        private set

    var created = Instant.DISTANT_PAST

    var tenantId = 0L
        private set

    private var _emails = mutableListOf<Email>()
    val emails: List<Email>
        get() = _emails

    private var _phoneNumbers = mutableListOf<PhoneNumber>()
    val phoneNumbers: List<PhoneNumber>
        get() = _phoneNumbers

    private var _primaryEmail: Email? = null
    var primaryEmail:Email?
        get() = _primaryEmail
        set(value) {
            if (value != null && !_emails.contains(value))
                throw InvalidStateException("Email must be added to contact before it can be set as primary")

            apply(PrimaryEmailSet(value))
        }

    private var _primaryPhoneNumber: PhoneNumber? = null
    var primaryPhoneNumber:PhoneNumber?
        get() = _primaryPhoneNumber
        set(value) {
            if (value != null && !_phoneNumbers.contains(value))
                throw InvalidStateException("Phone number must be added to contact before it can be set as primary")

            apply(PrimaryPhoneNumberSet(value))
        }

    fun rename(newName: String) = apply(ContactRenamed(newName))
    fun addEmail(email: Email) = apply(EmailAdded(email))
    fun addPhoneNumber(phoneNumber: PhoneNumber) = apply(PhoneNumberAdded(phoneNumber))
    fun removeEmail(email: Email) {
        if (!_emails.contains(email))
            throw InvalidStateException("Email not found in contact")
        apply(EmailRemoved(email))
    }
    fun removePhoneNumber(phoneNumber: PhoneNumber) = apply(PhoneNumberRemoved(phoneNumber))


    override fun update(event: Event) {
        when (event) {
            is NewContactCreated -> {
                id = event.id
                name = event.name
                created = event.created
                tenantId = event.tenantId
            }
            is EmailAdded -> _emails.add(event.email)
            is EmailRemoved -> {
                _emails.remove(event.email)
                if (_primaryEmail == event.email)
                    _primaryEmail = null
            }
            is PrimaryEmailSet -> _primaryEmail = event.email
            is PhoneNumberAdded -> _phoneNumbers.add(event.phoneNumber)
            is PhoneNumberRemoved -> {
                _phoneNumbers.remove(event.phoneNumber)
                if (_primaryPhoneNumber == event.phoneNumber)
                    _primaryPhoneNumber = null
            }
            is PrimaryPhoneNumberSet -> _primaryPhoneNumber = event.phoneNumber
            is ContactRenamed -> name = event.newName
        }
    }

    override fun ensureValidState() {
        if (id == DEFAULT_ID) {
            throw InvalidStateException("Contact ID must be set")
        }
        if (name == "") {
            throw InvalidStateException("Contact name is empty")
        }
    }
}