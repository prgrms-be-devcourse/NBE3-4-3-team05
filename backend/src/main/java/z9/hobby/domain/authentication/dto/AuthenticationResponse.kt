package z9.hobby.domain.authentication.dto

class AuthenticationResponse {

    data class UserToken(
        val accessToken: String,
        val refreshToken: String
    )
}