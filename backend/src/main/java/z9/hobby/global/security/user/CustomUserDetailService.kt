package z9.hobby.global.security.user

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import z9.hobby.model.user.User
import z9.hobby.model.user.UserRepository

@Service
class CustomUserDetailService(val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val userData: User? = userRepository.findByLoginId(username).orElse(null)

        return if (userData != null) {
            CustomUserDetails(userData)
        } else {
            null
        }
    }
}
