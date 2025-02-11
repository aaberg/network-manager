package net.aabergs.networkmanager.bl.contact

import kotlinx.datetime.Instant
import net.aabergs.networkmanager.bl.*
import net.aabergs.networkmanager.bl.InvalidStateException
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

    private val _emails = mutableListOf<Email>()
    val emails: List<Email>
        get() = _emails

    private val _phoneNumbers = mutableListOf<PhoneNumber>()
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

    var note = ""
        private set

    private val _logEntries = mutableListOf<LogEntry>()
    val logEntries: List<LogEntry>
        get() = _logEntries

    var isDeleted = false
        private set

    fun rename(newName: String) = apply(ContactRenamed(newName))
    fun addEmail(email: Email) = apply(EmailAdded(email))
    fun updateEmail(email: Email) = apply(EmailUpdated(email))
    fun setEmail(email: Email) {
        if (emails.any{it.id == email.id}) {
            apply(EmailUpdated(email))
        } else {
            apply(EmailAdded(email))
        }
    }
    fun addPhoneNumber(phoneNumber: PhoneNumber) = apply(PhoneNumberAdded(phoneNumber))
    fun removeEmail(email: Email) {
        if (!_emails.contains(email))
            throw InvalidStateException("Email not found in contact")
        apply(EmailRemoved(email))
    }
    fun removePhoneNumber(phoneNumber: PhoneNumber) = apply(PhoneNumberRemoved(phoneNumber))
    fun updateNote(note: String) = apply(NoteUpdated(note))
    fun delete() = apply(ContactDeleted())

    override fun update(event: Event) {
        when (event) {
            is NewContactCreated -> {
                id = event.id
                name = event.name
                created = event.created
                tenantId = event.tenantId
            }
            is EmailAdded -> _emails.add(event.email)
            is EmailUpdated -> {
                _emails[_emails.indexOfFirst { it.id == event.email.id }] = event.email
                if (_primaryEmail?.id == event.email.id)
                    _primaryEmail = event.email
            }
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
            is NoteUpdated -> note = event.note
            is ContactDeleted -> isDeleted = true
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