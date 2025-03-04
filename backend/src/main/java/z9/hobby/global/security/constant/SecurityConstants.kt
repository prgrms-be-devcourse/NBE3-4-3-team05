package z9.hobby.global.security.constant

object HeaderConstant {
    const val CONTENT_TYPE = "Content-Type"
}

object JWTConstant {
    const val ACCESS_TOKEN_HEADER = "Authorization"
    const val REFRESH_TOKEN_HEADER = "RefreshToken"
    const val ACCESS_TOKEN_PREFIX = "Bearer"

    const val CLAIM_KEY_USER_ID = "userId"
    const val CLAIM_KEY_USER_CATEGORY = "category"
    const val CLAIM_KEY_USER_ROLE = "role"

    const val ACCESS_TOKEN_CATEGORY = "accessToken"
    const val REFRESH_TOKEN_CATEGORY = "refreshToken"
}

object OAuthConstant {
    const val OAUTH_PROVIDER_GOOGLE = "google"
    const val OAUTH_PROVIDER_NAVER = "naver"
    const val OAUTH_PROVIDER_KAKAO = "kakao"
}