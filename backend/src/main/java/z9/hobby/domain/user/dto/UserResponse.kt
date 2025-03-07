package z9.hobby.domain.user.dto

import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.model.schedules.SchedulesEntity
import z9.hobby.model.user.User
import java.time.format.DateTimeFormatter

class UserResponse {

    data class UserInfo(
        val nickname: String,
        val type: String,
        val role: String,
        val createdAt: String,
        val favorite: List<String>
    ) {
        companion object {
            fun of(user: User, favorite: List<String>): UserInfo {
                val formattedDate = user.createdAt!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                return UserInfo(
                    nickname = user.nickname,
                    type = user.type.value,
                    role = user.role.value,
                    createdAt = formattedDate,
                    favorite = favorite
                )
            }
        }
    }

    data class UserSchedule(
        val schedule: List<ScheduleInfo>
    ) {
        companion object {
            fun from(schedule: List<ScheduleInfo>): UserSchedule {
                return UserSchedule(schedule = schedule)
            }
        }
    }

    data class ScheduleInfo(
        val classId: Long?,
        val meetingTime: String?,
        val meetingTitle: String?
    ) {
        companion object {
            fun from(schedulesEntity: SchedulesEntity): ScheduleInfo {
                return ScheduleInfo(
                    classId = schedulesEntity.getClasses().id,
                    meetingTime = schedulesEntity.getMeetingTime(),
                    meetingTitle = schedulesEntity.getMeetingTitle(),
                )
            }
        }
    }

    // UserClass 클래스
    data class UserClass(
        val classInfo: List<ClassInfo>
    ) {
        companion object {
            fun from(classInfo: List<ClassInfo>): UserClass {
                return UserClass(classInfo = classInfo)
            }
        }
    }

    data class ClassInfo(
        val classId: Long?,
        val name: String,
        val description: String,
        val favorite: String
    ) {
        companion object {
            fun from(classEntity: ClassEntity): ClassInfo {
                return ClassInfo(
                    classId = classEntity.id,
                    name = classEntity.name,
                    description = classEntity.description,
                    favorite = classEntity.favorite
                )
            }
        }
    }
}
