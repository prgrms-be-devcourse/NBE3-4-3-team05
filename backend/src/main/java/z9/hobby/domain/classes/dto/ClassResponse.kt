package z9.hobby.domain.classes.dto

import com.fasterxml.jackson.annotation.JsonProperty
import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.model.user.User

class ClassResponse {
    data class ClassResponseData(
        val id: Long?,
        val name: String,
        val favorite: String,
        val description: String
    ) {
        companion object {
            @JvmStatic
            fun from(classes: ClassEntity): ClassResponseData {
                return ClassResponseData(
                    id = classes.id,
                    name = classes.name,
                    favorite = classes.favorite,
                    description = classes.description
                )
            }
        }
    }

    data class EntryResponseData(
        val name: String,
        val favorite: String,
        val description: String
    ) {
        companion object {
            @JvmStatic
            fun from(classes: ClassEntity): EntryResponseData {
                return EntryResponseData(
                    name = classes.name,
                    favorite = classes.favorite,
                    description = classes.description
                )
            }
        }
    }

    data class JoinResponseData(
        val id: Long?,
        val name: String
    ) {
        companion object {
            @JvmStatic
            fun from(classes: ClassEntity): JoinResponseData {
                return JoinResponseData(
                    id = classes.id,
                    name = classes.name
                )
            }
        }
    }

    data class ClassUserListData(
        val classId: Long?,
        val name: String,
        val masterId: Long?,
        val userList: List<ClassUserInfo>
    ) {
        companion object {
            @JvmStatic
            fun from(classes: ClassEntity, users: List<User>): ClassUserListData {
                return ClassUserListData(
                    classId = classes.id,
                    name = classes.name,
                    masterId = classes.masterId,
                    userList = users.map { ClassUserInfo.from(it) }
                )
            }
        }
    }

    data class ClassUserInfo(
        val userId: Long?,
        val nickName: String
    ) {
        companion object {
            @JvmStatic
            fun from(user: User): ClassUserInfo {
                return ClassUserInfo(
                    userId = user.id,
                    nickName = user.nickname
                )
            }
        }
    }

    class CheckMemberData(
        @JsonProperty("member")
        val isMember: Boolean
    ) {
        companion object {
            @JvmStatic
            fun from(isMember: Boolean): CheckMemberData {
                return CheckMemberData(
                    isMember = isMember
                )
            }
        }
    }

    data class CheckBlackListData(
        @JsonProperty("blackListed")
        val isBlackListed: Boolean
    ) {
        companion object {
            @JvmStatic
            fun from(isBlackListed: Boolean): CheckBlackListData {
                return CheckBlackListData(
                    isBlackListed = isBlackListed
                )
            }
        }
    }
}
