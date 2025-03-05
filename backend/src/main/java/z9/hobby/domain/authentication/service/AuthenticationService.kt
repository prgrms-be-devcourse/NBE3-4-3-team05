package z9.hobby.domain.authentication.service

import z9.hobby.domain.authentication.dto.AuthenticationRequest
import z9.hobby.domain.authentication.dto.AuthenticationResponse

interface AuthenticationService {

    fun login(dto: AuthenticationRequest.Login): AuthenticationResponse.UserToken

    fun oauthLogin(provider: String, authCode: String): AuthenticationResponse.UserToken

    fun signup(signupDto: AuthenticationRequest.Signup)

    fun logout(userId: String)

    fun resign(userId: String)
}
