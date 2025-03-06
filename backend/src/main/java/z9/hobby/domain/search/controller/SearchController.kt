package z9.hobby.domain.search.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import z9.hobby.domain.search.SortBy
import z9.hobby.domain.search.dto.SearchResponseDto
import z9.hobby.domain.search.service.SearchService
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.BaseResponse.Companion.ok
import z9.hobby.global.response.SuccessCode
import java.security.Principal

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search Controller", description = "모임 검색 정렬 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class SearchController(
    private val searchService: SearchService
) {
    @GetMapping("/classes")
    @Operation(summary = "모임 리스트 조회", description = "정렬 조건에 따른 모임 리스트를 조회합니다.")
    fun searchClasses(
        @Parameter(description = "정렬 조건 (FAVORITE, CREATED_DESC, CREATED_ASC, NAME_ASC, PARTICIPANT_DESC)")
        @RequestParam(required = false) sortBy: SortBy?,
        principal: Principal?
    ): BaseResponse<List<SearchResponseDto>> {
        val userId = principal?.name?.toLongOrNull()
        val response = searchService.searchClasses(sortBy, userId)
        return ok(SuccessCode.CLASS_READ_SUCCESS, response)
    }

    @GetMapping("/favorite")
    @Operation(summary = "관심사 기반 모임 리스트 조회", description = "사용자의 관심사와 일치하는 모임 리스트를 조회합니다.")
    fun searchFavoriteClasses(principal: Principal): BaseResponse<List<SearchResponseDto>> {
        val userId = principal.name.toLong()
        val response = searchService.searchClasses(SortBy.FAVORITE, userId)
        return ok(SuccessCode.CLASS_READ_SUCCESS, response)
    }
}
