package net.aabergs.networkmanager.web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.web.invoke

@EnableWebSecurity
@Configuration
class SecurityConfig {

    @Bean
    fun userDetailService(passwordEncoder: PasswordEncoder) : UserDetailsService {
        val manager = InMemoryUserDetailsManager()

        manager.createUser(
            User.withUsername("aaberg")
                .password(passwordEncoder.encode("thepassword"))
                .roles("USER")
                .build()
        )

        manager.createUser(
            User.withUsername("admin")
                .password(passwordEncoder.encode("adminsecurepassword"))
                .roles("ADMIN")
                .build()
        )

        return manager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity) : SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/admin/**", hasRole("ADMIN"))
                authorize("/app/**", hasRole("USER"))
                authorize("/account/**", permitAll)
                authorize("/auth/**", permitAll)
                authorize("/", permitAll)

                // static resources
                authorize("/webjars/**", permitAll)
                authorize("/css/**", permitAll)
                authorize("/images/**", permitAll)
                authorize("/js/**", permitAll)

                // the rest
                authorize(anyRequest, denyAll)
            }
            formLogin {
                loginPage = "/auth/login"
                permitAll()
                defaultSuccessUrl("/auth/postlogin", true)
            }
        }

        return http.build()
    }
}