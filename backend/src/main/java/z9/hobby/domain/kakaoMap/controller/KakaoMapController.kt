package z9.hobby.domain.kakaoMap.controller;

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import z9.hobby.domain.kakaoMap.dto.KakaoMapDto
import z9.hobby.domain.kakaoMap.service.KakaoMapService
import z9.hobby.global.response.BaseResponse
import z9.hobby.global.response.SuccessCode
import java.security.Principal

@RestController
@RequestMapping("/api/v1/kakaomap")
@Tag(name = "KakaoMap Controller", description = "카카오맵 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
class KakaoMapController(
    private val kakaoMapService: KakaoMapService
) {
    @GetMapping
    @Operation(summary = "지도내에 해당하는 모임 일정 조회", description = "사용자가 지정한 관심사 혹은 전체 모임 일정을 조회합니다.")
    fun getLocationInfo(
        @RequestParam("filterType") filterType: String?,
        @RequestParam("dataRange") dataRange: String?,
        @RequestParam("bottomLeftLat") bottomLeftLat: Double,
        @RequestParam("bottomLeftLng") bottomLeftLng: Double,
        @RequestParam("topRightLat") topRightLat: Double,
        @RequestParam("topRightLng") topRightLng: Double,
        principal: Principal?
    ): BaseResponse<List<KakaoMapDto.SchedulesLocationData>> {
        val userId: Long? = principal?.name?.toLongOrNull()

        val locationData =
            kakaoMapService.getLatLngInfo(
                filterType,
                dataRange,
                bottomLeftLat,
                bottomLeftLng,
                topRightLat,
                topRightLng,
                userId
            )

        return BaseResponse.ok(SuccessCode.SUCCESS, locationData)
    }
}