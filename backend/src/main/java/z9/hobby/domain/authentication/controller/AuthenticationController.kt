package z9.hobby.domain.authentication.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import z9.hobby.domain.authentication.dto.AuthenticationRequest
import z9.hobby.domain.authentication.dto.AuthenticationResponse
import z9.hobby.domain.authentication.service.AuthenticationService
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.SuccessCode
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_HEADER
import z9.hobby.global.security.constant.JWTConstant.REFRESH_TOKEN_HEADER
import z9.hobby.global.security.jwt.JwtProperties
import z9.hobby.global.utils.ControllerUtils
import java.security.Principal

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Authentication Controller", description = "회원 인증 컨트롤러")
class AuthenticationController(
    private val authenticationService: AuthenticationService,
    private val jwtProperties: JwtProperties
) {

    @PostMapping("/login")
    @Operation(summary = "회원 로그인")
    fun login(
        @Valid @RequestBody dto: AuthenticationRequest.Login,
        response: HttpServletResponse
    ): BaseResponse<Nothing> {
        val token = authenticationService.login(dto)
        addJwtTokenResponse(response, token)
        return BaseResponse.ok(SuccessCode.LOGIN_SUCCESS)
    }

    @GetMapping("/login/{provider}")
    @Operation(summary = "소셜 로그인 리다이렉트 주소")
    fun kakaoLogin(
        @PathVariable provider: String,
        @RequestParam code: String,
        response: HttpServletResponse
    ): BaseResponse<Nothing> {
        val token = authenticationService.oauthLogin(provider, code)
        addJwtTokenResponse(response, token)
        return BaseResponse.ok(SuccessCode.LOGIN_SUCCESS)
    }

    @PostMapping("/signup")
    @Operation(summary = "일반 회원 가입")
    fun signup(
        @Valid @RequestBody signupDto: AuthenticationRequest.Signup
    ): BaseResponse<Nothing> {
        authenticationService.signup(signupDto)
        return BaseResponse.ok(SuccessCode.SIGNUP_SUCCESS)
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    @SecurityRequirement(name = "bearerAuth")
    fun logout(
        response: HttpServletResponse,
        principal: Principal
    ): BaseResponse<Nothing> {
        authenticationService.logout(principal.name)
        deleteRefreshTokenCookie(response)
        return BaseResponse.ok(SuccessCode.LOGOUT_SUCCESS)
    }

    @PatchMapping("/resign")
    @Operation(summary = "회원 탈퇴")
    @SecurityRequirement(name = "bearerAuth")
    fun resign(principal: Principal): BaseResponse<Nothing> {
        authenticationService.resign(principal.name)
        return BaseResponse.ok(SuccessCode.RESIGN_SUCCESS)
    }

    private fun addJwtTokenResponse(response: HttpServletResponse, token: AuthenticationResponse.UserToken) {
        ControllerUtils.addHeaderResponse(
            ACCESS_TOKEN_HEADER,
            ControllerUtils.makeBearerToken(token.accessToken),
            response
        )
        ControllerUtils.addCookieResponse(
            REFRESH_TOKEN_HEADER,
            token.refreshToken,
            ControllerUtils.parseMsToSec(jwtProperties.refreshExpiration),
            response
        )
    }

    private fun deleteRefreshTokenCookie(response: HttpServletResponse) {
        ControllerUtils.addCookieResponse(
            REFRESH_TOKEN_HEADER,
            null,
            0,
            response
        )
    }
}