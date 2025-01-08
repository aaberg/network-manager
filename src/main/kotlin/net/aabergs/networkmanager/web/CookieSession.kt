package net.aabergs.networkmanager.web

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.Principal

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class CookieSession(val request: HttpServletRequest, val response: HttpServletResponse) {
    companion object {
        const val COOKIE_NAME = "pubsession"
    }

    var tenantId: Long
        get() {
            return pubSession.tenantId
        }
        set(value) {
            pubSession = pubSession.copy(tenantId = value)
            updateCookie()
        }

    private var _pubSession:PubSession? = null
    private var pubSession: PubSession
        get() {
            if (_pubSession == null) {
                _pubSession = request.cookies?.find { it.name == COOKIE_NAME}?.let {
                    val cookieVal = URLDecoder.decode(it.value, Charsets.UTF_8)
                    Json.decodeFromString(PubSession.serializer(), cookieVal)
                } ?: PubSession(0L);
            }
            return _pubSession!!
        }
        set(value) {
            _pubSession = value
        }


    //private var pubSession = PubSession(0)
    private fun updateCookie() {
        val jsonEncodedSession = Json.encodeToString(PubSession.serializer(), pubSession)
        val cookie = Cookie(COOKIE_NAME, URLEncoder.encode(jsonEncodedSession, Charsets.UTF_8))
        cookie.secure = true
        cookie.path = "/"
        response.addCookie(cookie)
    }

}

@Serializable data class PubSession(val tenantId: Long)