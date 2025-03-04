package z9.hobby.global.security.oauth.user

import z9.hobby.model.oauthuser.OAuthProvider

interface OAuth2UserInfo {
    fun getProviderId(): String
    fun getProvider(): OAuthProvider
    fun getName(): String
}
