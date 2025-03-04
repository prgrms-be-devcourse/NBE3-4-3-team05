package z9.hobby.global.security.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import z9.hobby.model.user.User

class CustomUserDetails(val user: User) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(GrantedAuthority { user.role.name })
    }

    override fun getPassword(): String {
        return user.password.toString()
    }

    override fun getUsername(): String {
        return user.id.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
