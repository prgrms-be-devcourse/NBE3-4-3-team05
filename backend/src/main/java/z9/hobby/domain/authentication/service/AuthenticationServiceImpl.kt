package z9.hobby.domain.authentication.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.authentication.dto.AuthenticationRequest
import z9.hobby.domain.authentication.dto.AuthenticationResponse
import z9.hobby.domain.classes.repository.ClassRepository
import z9.hobby.domain.classes.repository.ClassUserRepository
import z9.hobby.domain.favorite.repository.FavoriteRepository
import z9.hobby.domain.kakao.KakaoAuthFeignClient
import z9.hobby.domain.kakao.KakaoResourceFeignClient
import z9.hobby.global.exception.CustomException
import z9.hobby.global.redis.RedisRepository
import z9.hobby.global.response.ErrorCode
import z9.hobby.global.security.constant.JWTConstant.ACCESS_TOKEN_CATEGORY
import z9.hobby.global.security.constant.JWTConstant.REFRESH_TOKEN_CATEGORY
import z9.hobby.global.security.constant.OAuthConstant
import z9.hobby.global.security.jwt.JWTUtil
import z9.hobby.global.security.jwt.JwtProperties
import z9.hobby.global.security.oauth.OAuthProperties
import z9.hobby.global.security.oauth.user.KakaoUserInfo
import z9.hobby.global.security.oauth.user.OAuth2UserInfo
import z9.hobby.global.security.user.CustomUserDetails
import z9.hobby.global.utils.ControllerUtils
import z9.hobby.model.oauthuser.OAuthUser
import z9.hobby.model.oauthuser.OAuthUserRepository
import z9.hobby.model.user.User
import z9.hobby.model.user.UserRepository
import z9.hobby.model.user.UserStatus
import z9.hobby.model.userfavorite.UserFavorite
import z9.hobby.model.userfavorite.UserFavoriteRepository

@Service
class AuthenticationServiceImpl(
    private val jwtProperties: JwtProperties,
    private val oAuthProperties: OAuthProperties,
    private val jwtUtil: JWTUtil,
    private val authenticationManager: AuthenticationManager,
    private val kakaoAuthFeignClient: KakaoAuthFeignClient,
    private val kakaoResourceFeignClient: KakaoResourceFeignClient,
    private val oAuthUserRepository: OAuthUserRepository,
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository,
    private val redisRepository: RedisRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userFavoriteRepository: UserFavoriteRepository,
    private val classUserRepository: ClassUserRepository,
    private val classRepository: ClassRepository
) : AuthenticationService {

    @Transactional(readOnly = true)
    override fun login(dto: AuthenticationRequest.Login): AuthenticationResponse.UserToken {
        val authentication = authenticateUser(dto)
        val userDetails = authentication.principal as CustomUserDetails
        return generateUserTokens(
            authentication.authorities.first().authority,
            userDetails.username
        )
    }

    @Transactional
    override fun oauthLogin(provider: String, authCode: String): AuthenticationResponse.UserToken {
        val oauth2UserInfo = getOauth2UserInfo(provider, authCode)
        val user = getUserByOAuth(oauth2UserInfo)
        if (user.status == UserStatus.DELETE) throw CustomException(ErrorCode.LOGIN_RESIGN_USER)
        return generateUserTokens(user.role.name, user.id.toString())
    }

    @Transactional
    override fun signup(signupDto: AuthenticationRequest.Signup) {
        val favorite = signupDto.favorite
        val findFavorites = favoriteRepository.findByNameIn(favorite)
        if (findFavorites.size != favorite.size) throw CustomException(ErrorCode.NOT_EXIST_FAVORITE)

        userRepository.findByLoginIdOrNickname(signupDto.loginId, signupDto.nickname)?.let {
            if (it.loginId == signupDto.loginId) {
                throw CustomException(ErrorCode.DUPLICATED_LOGIN_ID)
            }
            if (it.nickname == signupDto.nickname) {
                throw CustomException(ErrorCode.DUPLICATED_NICKNAME)
            }
        }

        val newUser = User.createNewUser(
            signupDto.loginId,
            passwordEncoder.encode(signupDto.password),
            signupDto.nickname
        )
        val savedUser = userRepository.save(newUser)

        val userFavoriteList =
            findFavorites.map { UserFavorite.createNewUserFavorite(savedUser, it) }
        userFavoriteRepository.saveAll(userFavoriteList)
    }

    @Transactional
    override fun logout(userId: String) {
        redisRepository.deleteRefreshToken(userId)
    }

    @Transactional
    override fun resign(userId: String) {
        val findUser = userRepository.findById(userId.toLong())
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        if (findUser.status == UserStatus.DELETE) throw CustomException(ErrorCode.ALREADY_DELETE_USER)
        if (classRepository.existsByMasterId(findUser.id ?: throw CustomException(ErrorCode.USER_NOT_FOUND))) {
            throw CustomException(ErrorCode.CLASS_MASTER_TRANSFER_REQUIRED)
        }

        userRepository.save(User.resign(findUser))
        cleanupUserAssociations(findUser.id!!)
    }

    private fun cleanupUserAssociations(id: Long) {
        classUserRepository.deleteByUserId(id)
        userFavoriteRepository.deleteByUserId(id)
    }

    private fun getUserByOAuth(oauth2UserInfo: OAuth2UserInfo): User {
        return oAuthUserRepository.findByProviderAndUid(
            oauth2UserInfo.getProvider(),
            oauth2UserInfo.getProviderId()
        )?.user
            ?: createNewUserAndOAuthUser(oauth2UserInfo)
    }

    private fun getOauth2UserInfo(provider: String, authCode: String): OAuth2UserInfo {
        return when (provider) {
            OAuthConstant.OAUTH_PROVIDER_KAKAO -> {
                val oAuthToken = kakaoAuthFeignClient.getKakaoToken(
                    "authorization_code", oAuthProperties.clientId, oAuthProperties.redirectUri,
                    authCode, oAuthProperties.clientSecret, oAuthProperties.contentType
                )
                val userInfoMap = kakaoResourceFeignClient.getUserInfo(
                    ControllerUtils.makeBearerToken(oAuthToken.accessToken),
                    oAuthProperties.contentType
                )
                KakaoUserInfo(userInfoMap)
            }

            else -> throw CustomException(ErrorCode.INVALID_OAUTH_PROVIDER)
        }
    }

    private fun createNewUserAndOAuthUser(oauth2UserInfo: OAuth2UserInfo): User {
        val savedUser = userRepository.save(
            User.createNewOAuthUser(
                oauth2UserInfo.getName(),
                oauth2UserInfo.getProviderId().take(5)
            )
        )
        oAuthUserRepository.save(
            OAuthUser.createNewOAuthUser(
                oauth2UserInfo.getProviderId(),
                oauth2UserInfo.getProvider(),
                savedUser
            )
        )
        return savedUser
    }

    private fun authenticateUser(dto: AuthenticationRequest.Login): Authentication {
        val authToken = UsernamePasswordAuthenticationToken(dto.loginId, dto.password)
        return authenticationManager.authenticate(authToken)
    }

    private fun generateUserTokens(role: String, userId: String): AuthenticationResponse.UserToken {
        val access =
            jwtUtil.createJwt(ACCESS_TOKEN_CATEGORY, userId, role, jwtProperties.accessExpiration)
        val refresh =
            jwtUtil.createJwt(REFRESH_TOKEN_CATEGORY, userId, role, jwtProperties.refreshExpiration)
        saveRefreshToken(userId, refresh, jwtProperties.refreshExpiration)
        return AuthenticationResponse.UserToken(access, refresh)
    }

    private fun saveRefreshToken(userId: String, refreshToken: String, expirationMs: Long) {
        redisRepository.saveRefreshToken(userId, refreshToken, expirationMs)
    }
}
