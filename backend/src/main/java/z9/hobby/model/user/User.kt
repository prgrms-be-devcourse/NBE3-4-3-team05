package z9.hobby.model.user

import jakarta.persistence.*
import z9.hobby.model.BaseEntity

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "login_id", unique = true)
    val loginId: String? = null,

    @Column(name = "password")
    val password: String? = null,

    @Column(name = "nickname", nullable = false, unique = true, length = 10)
    val nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: UserType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: UserStatus,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: UserRole
) : BaseEntity() {
    companion object {
        @JvmStatic
        fun createNewUser(loginId: String, password: String, nickname: String) = User(
            loginId = loginId,
            password = password,
            nickname = nickname,
            type = UserType.NORMAL,
            status = UserStatus.ACTIVE,
            role = UserRole.ROLE_USER,
        )

        @JvmStatic
        fun createNewOAuthUser(nickname: String, hashCode: String) = User(
            nickname = "$nickname$hashCode",
            type = UserType.OAUTH,
            status = UserStatus.ACTIVE,
            role = UserRole.ROLE_USER
        )

        @JvmStatic
        fun createSecurityContextUser(userId: Long, userRole: UserRole) = User(
            id = userId,
            nickname = "",
            type = UserType.NORMAL,
            status = UserStatus.ACTIVE,
            role = userRole
        )

        @JvmStatic
        fun resign(user: User) = user.copy(
            status = UserStatus.DELETE
        )

        @JvmStatic
        fun patchUserInfo(user: User, nickname: String) = user.copy(
            nickname = nickname
        )
    }
}
