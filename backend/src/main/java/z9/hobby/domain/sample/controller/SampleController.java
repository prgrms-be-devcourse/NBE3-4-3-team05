package z9.hobby.domain.sample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import z9.hobby.domain.sample.dto.SampleRequest;
import z9.hobby.domain.sample.dto.SampleResponse;
import z9.hobby.domain.sample.service.SampleService;
import z9.hobby.global.response.BaseResponse;
import z9.hobby.global.response.SuccessCode;

@RestController
@RequestMapping("/api/v1/sample")
@Tag(name = "Sample Controller", description = "API 샘플 컨트롤러")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    /**
    //sample ID로 단건 찾기
    /*
        {
            "isSuccess": true,
            "message": "샘플 데이터 찾기 성공!",
            "code": 200,
            "data": {
                "fullName": "김-아무개1"
            }
        }
    */
    @GetMapping("/{sampleId}")
    @Operation(summary = "특정 Sample 정보 조회")
    public BaseResponse<SampleResponse.SampleDataInfo> getSampleData(
            @PathVariable("sampleId") Long sampleId
    ) {
        SampleResponse.SampleDataInfo data = sampleService.findSampleById(sampleId);
        return BaseResponse.Companion.ok(SuccessCode.FIND_SAMPLE_DATA_SUCCESS, data);
    }

    /**
    //저장된 모든 sample data 들 읽어오기
    /*
        {
            "isSuccess": true,
            "message": "샘플 전체 데이터 찾기 성공!",
            "code": 200,
            "data": [
                {
                    "fullName": "김-아무개1",
                    "id": 1
                },
                {
                    "fullName": "김-아무개2",
                    "id": 2
                }...
            ]
        }
    */
    @GetMapping
    @Operation(summary = "Sample 목록 조회")
    public BaseResponse<List<SampleResponse.SampleDataList>> getSampleDataList() {
        List<SampleResponse.SampleDataList> findList = sampleService.findAllSampleData();
        return BaseResponse.Companion.ok(SuccessCode.FIND_SAMPLE_DATA_LIST_SUCCESS, findList);
    }

    /**
    //Sample 데이터 저장하기
    /*
        request :
         {
         "firstName" : "강",
         "secondName" : "성욱",
         "age" : 20
         }
        response :
         {
         "isSuccess": true,
         "message": "샘플 데이터 저장 성공!",
         "code": 200,
         "data": {
         "id": 11
         }
         }
    */
    @PostMapping
    @Operation(summary = "Sample 등록")
    public BaseResponse<SampleResponse.SavedSampleData> registerNewSample(
            @RequestBody(required = true) SampleRequest.NewSampleData newSampleData
    ) {
        SampleResponse.SavedSampleData savedSampleData
                = sampleService.saveNewSampleData(newSampleData);
        return BaseResponse.Companion.ok(SuccessCode.SAVE_SAMPLE_DATA_SUCCESS, savedSampleData);
    }

    /**
     * 회원만 접근 가능한, end Point
     * 회원만 접근 가능하게 막은 역할은, `SecurityConfig`에, `.requestMatchers()` 로 설정된 부분이 영향을 줍니다.
     * 또한, 내부적으로는, accessToken 을 활용하여 SecurityContext 에 인증된 회원의 정보를 넣도록 되어있습니다.
     *      `AuthenticationFilter` 참조!
     * 결과적으로, Controller 에서, `Principal` 을 다음과 같이 받아와서 인증된 회원의 정보를 조회할 수 있습니다.
     * 현재는, principal.getName() 을 하면, 회원의 고유 식별 id (userId) 가 나오게 됩니다.
     * @param principal
     * @return
     */
    @GetMapping("/only-user")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "회원만 접근 가능한 endPoint")
    public BaseResponse<Void> getSampleDataOnlyUser(
            Principal principal
    ) {
        System.out.println("userId : " + principal.getName());
        return BaseResponse.Companion.ok(SuccessCode.SUCCESS);
    }

    /**
     * admin 만 접근 가능한 endpoint 입니다.
     * `SecurityConfig`에, `.requestMatchers` 로 설정된 부분이 영향을 줍니다.
     * @param principal
     * @return
     */
    @GetMapping("/only-admin")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "회원만 접근 가능한 endPoint")
    public BaseResponse<Void> getSampleDataOnlyAdmin(
            Principal principal
    ) {
        System.out.println("admin userId : " + principal.getName());
        return BaseResponse.Companion.ok(SuccessCode.SUCCESS);
    }
}
