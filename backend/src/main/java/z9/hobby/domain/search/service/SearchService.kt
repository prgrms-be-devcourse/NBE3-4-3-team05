package z9.hobby.domain.search.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.domain.classes.repository.ClassRepository
import z9.hobby.domain.search.SortBy
import z9.hobby.domain.search.dto.SearchResponseDto
import z9.hobby.global.exception.CustomException
import z9.hobby.global.response.ErrorCode
import z9.hobby.model.userfavorite.UserFavoriteRepository

@Service
class SearchService(
    private val classRepository: ClassRepository,
    private val userFavoriteRepository: UserFavoriteRepository
) {
    @Transactional(readOnly = true)
    fun searchClasses(sortBy: SortBy?, userId: Long?): List<SearchResponseDto> {
        // 로그인 상태에 따른 기본 정렬 설정
        val effectiveSortBy = sortBy ?: if (userId != null) SortBy.FAVORITE else SortBy.CREATED_DESC

        try {
            val classes: List<ClassEntity> = when (effectiveSortBy) {
                SortBy.FAVORITE -> {
                    if (userId != null) {
                        // 로그인: 사용자의 관심사와 일치하는 모임만 조회
                        val userFavorites = userFavoriteRepository.findFavoriteNamesByUserId(userId)
                        classRepository.findByUserFavorites(userFavorites)
                    } else {
                        // 비로그인: 전체 모임을 관심사별, 가나다순 정렬
                        classRepository.findByFavorites()
                    }
                }
                SortBy.NAME_ASC -> classRepository.findAllByOrderByName()
                SortBy.PARTICIPANT_DESC -> classRepository.findByParticipantSort()
                SortBy.CREATED_ASC -> classRepository.findAllByOrderByCreatedAtAsc()
                SortBy.CREATED_DESC -> classRepository.findAllByOrderByCreatedAtDesc()
                else -> classRepository.findAllByOrderByCreatedAtDesc()
            }

            return classes.map { SearchResponseDto.from(it) }
        } catch (e: Exception) {
            throw CustomException(ErrorCode.CLASS_READ_FAILED).apply {
                initCause(e) // 원본 예외를 cause로 설정
            }
        }
    }
}
