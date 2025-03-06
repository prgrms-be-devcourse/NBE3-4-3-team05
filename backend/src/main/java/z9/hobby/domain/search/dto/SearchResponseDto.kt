package z9.hobby.domain.search.dto

import z9.hobby.domain.classes.entity.ClassEntity
import java.time.LocalDateTime

data class SearchResponseDto(
    val id: Long?,
    val name: String?,
    val favorite: String?,
    val description: String?,
    val participantCount: Int,
    val masterId: Long?,
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(entity: ClassEntity): SearchResponseDto {
            return SearchResponseDto(
                id = entity.id,
                name = entity.name,
                favorite = entity.favorite,
                description = entity.description,
                participantCount = entity.users.size,
                masterId = entity.masterId,
                createdAt = entity.createdAt
            )
        }
    }
}
