package z9.hobby.domain.favorite.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.favorite.dto.FavoriteResponse;
import z9.hobby.domain.favorite.service.FavoriteService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
@Tag(name = "Favorite Controller", description = "관심사 컨트롤러")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "관심사 조회")
    public BaseResponse<List<FavoriteResponse.ResponseData>> getFavorites() {
        List<FavoriteResponse.ResponseData> favoriteList = favoriteService.findAll();

        return BaseResponse.Companion.ok(SuccessCode.SUCCESS, favoriteList);
    }
}
