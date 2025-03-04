package z9.hobby.global.security.oauth.user

import z9.hobby.model.oauthuser.OAuthProvider

data class KakaoUserInfo(
    private val attributes: Map<String, Any>
) : OAuth2UserInfo {

    override fun getProviderId(): String {
        return attributes["id"].toString()
    }

    override fun getProvider(): OAuthProvider {
        return OAuthProvider.KAKAO
    }

    override fun getName(): String {
        return (attributes["properties"] as Map<*, *>).get("nickname") as String
    }
}
