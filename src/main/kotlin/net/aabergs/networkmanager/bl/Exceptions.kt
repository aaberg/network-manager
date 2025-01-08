package net.aabergs.networkmanager.bl

class InvalidStateException(message: String) : Exception(message)

class UnknownTenantException(message: String) : Exception(message)

class AccessDeniedException(message: String) : Exception(message)