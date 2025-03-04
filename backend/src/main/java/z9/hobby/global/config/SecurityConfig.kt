package z9.hobby.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import z9.hobby.global.security.constant.HeaderConstant.CONTENT_TYPE
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_HEADER
import z9.hobby.global.security.entrypoint.CustomAccessDeniedEntryPoint
import z9.hobby.global.security.entrypoint.CustomAuthenticationEntryPoint
import z9.hobby.global.security.filter.AuthenticationFilter
import z9.hobby.global.security.filter.ReissueFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationFilter: AuthenticationFilter,
    private val reissueFilter: ReissueFilter,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDeniedEntryPoint: CustomAccessDeniedEntryPoint
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager =
        configuration.authenticationManager

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowCredentials = true
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf(ACCESS_TOKEN_HEADER, CONTENT_TYPE)
        configuration.exposedHeaders = listOf(ACCESS_TOKEN_HEADER)
        configuration.maxAge = 3600L

        return CorsConfigurationSource { request -> configuration }
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        http.sessionManagement { session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.cors { it.configurationSource(corsConfigurationSource()) }

        http.authorizeHttpRequests { auth ->
            auth
                .requestMatchers(GET, "/api/v1/sample/only-user").authenticated()
                .requestMatchers(GET, "/api/v1/sample/only-admin").hasRole("ADMIN")

                // authentication Domain
                .requestMatchers(POST, "/api/v1/logout").authenticated()
                .requestMatchers(PATCH, "/api/v1/resign").authenticated()

                // user Domain
                .requestMatchers(GET, "/api/v1/users").authenticated()
                .requestMatchers(PATCH, "/api/v1/users/profile").authenticated()
                .requestMatchers(GET, "/api/v1/users/schedules").authenticated()
                .requestMatchers(GET, "/api/v1/users/classes").authenticated()

                // class Domain
                // All classes in the Domain package require authentication
                .requestMatchers("/api/v1/classes/**").authenticated()

                // schedules Domain
                // All schedules in the Domain package require authentication
                .requestMatchers("/api/v1/schedules/**").authenticated()

                // search Domain
                .requestMatchers(GET, "/api/v1/search/favorite").authenticated()

                // checkin Domain
                // All checkIn in the Domain package require authentication
                .requestMatchers("/api/v1/checkin/**").authenticated()

                // 나머지 다 permitAll
                .anyRequest().permitAll()
        }

        http.exceptionHandling { exception ->
            exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedEntryPoint)
        }

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        http.addFilterBefore(reissueFilter, AuthenticationFilter::class.java)

        return http.build()
    }
}
