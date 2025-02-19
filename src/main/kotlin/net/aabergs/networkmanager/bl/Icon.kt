package net.aabergs.networkmanager.bl

import kotlinx.serialization.Serializable

@Serializable
enum class Icon {
    Email, Phone, TextMessage, SocialMedia, IRL, Other
}