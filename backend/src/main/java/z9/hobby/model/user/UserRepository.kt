package z9.hobby.model.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByLoginId(loginId: String): User?
    fun findByLoginIdOrNickname(loginId: String, nickname: String): User?
}
