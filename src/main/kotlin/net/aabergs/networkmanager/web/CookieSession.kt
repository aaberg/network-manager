package net.aabergs.networkmanager.web

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class CookieSession(request: HttpServletRequest) {
    init {
        request.cookies?.find { it.name == "pubsession" }?.let {
            tenantId = it.value
        }
    }

    var tenantId: String? = null
}