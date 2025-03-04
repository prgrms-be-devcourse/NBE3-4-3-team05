package z9.hobby.domain.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.search.SortBy;
import z9.hobby.domain.search.dto.SearchResponseDto;
import z9.hobby.domain.search.service.SearchService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search Controller", description = "모임 검색 정렬 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/classes")
    @Operation(summary = "모임 리스트 조회", description = "정렬 조건에 따른 모임 리스트를 조회합니다.")
    public BaseResponse<List<SearchResponseDto>> searchClasses(
            @Parameter(description = "정렬 조건 (FAVORITE, CREATED_DESC, CREATED_ASC, NAME_ASC, PARTICIPANT_DESC)")
            @RequestParam(required = false)SortBy sortBy,
            Principal principal
    ) {
        Long userId = null;
        if (principal != null) {
            userId = Long.parseLong(principal.getName());
        }
        List<SearchResponseDto> response = searchService.searchClasses(sortBy, userId);
        return BaseResponse.Companion.ok(SuccessCode.CLASS_READ_SUCCESS, response);
    }

    @GetMapping("/favorite")
    @Operation(summary = "관심사 기반 모임 리스트 조회", description = "사용자의 관심사와 일치하는 모임 리스트를 조회합니다.")
    public BaseResponse<List<SearchResponseDto>> searchFavoriteClasses(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        List<SearchResponseDto> response = searchService.searchClasses(SortBy.FAVORITE, userId);
        return BaseResponse.Companion.ok(SuccessCode.CLASS_READ_SUCCESS, response);
    }

}
