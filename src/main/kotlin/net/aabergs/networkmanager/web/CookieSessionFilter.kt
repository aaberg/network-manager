package net.aabergs.networkmanager.web

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

//@Component
//@Order(1)
//class CookieSessionFilter() : Filter {
//
//    @Lazy
//    private lateinit var cookieSession: CookieSession
//
//    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
//        if (request is HttpServletRequest) {
//            request.cookies?.find { it.name == "pubsession" }?.let {
//                cookieSession.tenantId = it.value
//            }
//        }
//    }
//}