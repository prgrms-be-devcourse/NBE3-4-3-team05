package z9.hobby.model.oauthuser

import jakarta.persistence.*
import z9.hobby.model.BaseEntity
import z9.hobby.model.user.User

@Entity
@Table(name = "users_oauth")
data class OAuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_oauth_id")
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    val provider: OAuthProvider,

    @Column(name = "uid", unique = true)
    val uid: String,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User
) : BaseEntity() {

    companion object {
        @JvmStatic
        fun createNewOAuthUser(uid: String, provider: OAuthProvider, user: User): OAuthUser {
            return OAuthUser(uid = uid, provider = provider, user = user)
        }
    }
}
