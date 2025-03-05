package z9.hobby.domain.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.classes.repository.ClassRepository
import z9.hobby.domain.favorite.repository.FavoriteRepository
import z9.hobby.domain.user.dto.UserRequest
import z9.hobby.domain.user.dto.UserResponse
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.schedules.SchedulesRepository
import z9.hobby.model.user.User
import z9.hobby.model.user.UserRepository
import z9.hobby.model.userfavorite.UserFavorite
import z9.hobby.model.userfavorite.UserFavoriteRepository

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userFavoriteRepository: UserFavoriteRepository,
    private val favoriteRepository: FavoriteRepository,
    private val schedulesRepository: SchedulesRepository,
    private val classRepository: ClassRepository
) : UserService {

    @Transactional(readOnly = true)
    override fun findUserInfo(userId: Long): UserResponse.UserInfo {
        val findUser = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val favorites = userFavoriteRepository.findFavoriteNamesByUserId(userId)

        return UserResponse.UserInfo.of(findUser, favorites)
    }

    @Transactional
    override fun patchUserInfo(requestDto: UserRequest.PatchUserInfo, userId: Long) {
        // 1. 회원 정보 수정
        val findUser = userRepository.findById(userId)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }

        val newUser = User.patchUserInfo(findUser, requestDto.nickname)
        val savedUser = userRepository.save(newUser)

        // 2. favorite 가 등록되어있는건지 확인
        val favorite = requestDto.favorite
        val findFavorites = favoriteRepository.findByNameIn(favorite)
        if (findFavorites.size != favorite.size) {
            throw CustomException(ErrorCode.NOT_EXIST_FAVORITE)
        }

        // 3. 회원 관심사 수정
        // - 해당 부분은 PUT 처럼, 전달 받은 걸로 전체 대체 진행 합니다.
        userFavoriteRepository.deleteByUserId(userId)
        val userFavoriteList = findFavorites.map { findFavorite ->
            UserFavorite.createNewUserFavorite(savedUser, findFavorite)
        }
        userFavoriteRepository.saveAll(userFavoriteList)
    }

    @Transactional(readOnly = true)
    override fun findUserSchedules(userId: Long): UserResponse.UserSchedule {
        val findData = schedulesRepository.findUserSchedulesInfoByUserId(userId)

        val scheduleInfoList = findData.map { UserResponse.ScheduleInfo.from(it) }

        return UserResponse.UserSchedule.from(scheduleInfoList)
    }

    @Transactional(readOnly = true)
    override fun findUserClasses(userId: Long): UserResponse.UserClass {
        val findData = classRepository.findByUserId(userId)

        val classInfoList = findData.map { UserResponse.ClassInfo.from(it) }

        return UserResponse.UserClass.from(classInfoList)
    }
}
