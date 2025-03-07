package z9.hobby.domain.user.service

import z9.hobby.domain.user.dto.UserRequest.PatchUserInfo
import z9.hobby.domain.user.dto.UserResponse
import z9.hobby.domain.user.dto.UserResponse.UserClass
import z9.hobby.domain.user.dto.UserResponse.UserSchedule

interface UserService {
    fun findUserInfo(userId: Long): UserResponse.UserInfo

    fun patchUserInfo(requestDto: PatchUserInfo, userId: Long)

    fun findUserSchedules(userId: Long): UserSchedule

    fun findUserClasses(userId: Long): UserClass
}
