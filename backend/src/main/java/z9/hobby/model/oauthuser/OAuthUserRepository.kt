package z9.hobby.model.oauthuser

import org.springframework.data.jpa.repository.JpaRepository

interface OAuthUserRepository : JpaRepository<OAuthUser, Long> {

    fun findByProviderAndUid(provider: OAuthProvider, uid: String): OAuthUser?
}
