package net.aabergs.networkmanager.bl.extensions

import net.aabergs.networkmanager.bl.contact.ContactAggregate

public fun ContactAggregate.primaryOrFirstEmail() : String {
    this.primaryEmail?.let { return it.email }
    this.emails.firstOrNull()?.let { return it.email }
    return "Not registered"
}

public fun ContactAggregate.primaryOrFirstPhoneNumber() : String {
    this.primaryPhoneNumber?.let { return it.number }
    this.phoneNumbers.firstOrNull()?.let { return it.number }
    return "Not registered"
}