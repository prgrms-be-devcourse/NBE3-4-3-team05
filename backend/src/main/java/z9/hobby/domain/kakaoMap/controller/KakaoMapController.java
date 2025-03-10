package z9.hobby.domain.kakaoMap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.kakaoMap.dto.KakaoMapDto;
import z9.hobby.domain.kakaoMap.service.KakaoMapService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kakaomap")
@Tag(name = "KakaoMap Controller", description = "카카오맵 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class KakaoMapController {
    private final KakaoMapService kakaoMapService;

    @GetMapping
    @Operation(summary = "지도내에 해당하는 모임 일정 조회", description = "사용자가 지정한 관심사 혹은 전체 모임 일정을 조회합니다.")
    public BaseResponse<List<KakaoMapDto.SchedulesLocationData>> getLocationInfo(
            @RequestParam("filterType") String filterType,
            @RequestParam("bottomLeftLat") double bottomLeftLat,
            @RequestParam("bottomLeftLng") double bottomLeftLng,
            @RequestParam("topRightLat") double topRightLat,
            @RequestParam("topRightLng") double topRightLng,
            Principal principal
    ) {
        Long userId = null;
        if (principal != null) {
            userId = Long.parseLong(principal.getName());
        }

        List<KakaoMapDto.SchedulesLocationData> locationData =
                kakaoMapService.getLatLngInfo(filterType, bottomLeftLat, bottomLeftLng, topRightLat, topRightLng, userId);

        return BaseResponse.Companion.ok(SuccessCode.SUCCESS, locationData);
    }
}