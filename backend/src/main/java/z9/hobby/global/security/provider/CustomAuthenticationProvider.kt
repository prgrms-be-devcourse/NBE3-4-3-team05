package z9.hobby.global.security.provider

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.global.security.user.CustomUserDetails
import z9.hobby.model.user.UserStatus

@Component
class CustomAuthenticationProvider(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        val loginId = authentication.name
        val password = authentication.credentials.toString()

        val userDetails: UserDetails = userDetailsService.loadUserByUsername(loginId)
            ?: throw CustomException(ErrorCode.LOGIN_FAIL)

        val customUserDetails = userDetails as CustomUserDetails
        if (customUserDetails.user.getStatus() == UserStatus.DELETE) {
            throw CustomException(ErrorCode.LOGIN_RESIGN_USER)
        }

        if (!passwordEncoder.matches(password, userDetails.password)) {
            throw CustomException(ErrorCode.LOGIN_FAIL)
        }

        return UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
