package z9.hobby.global.response

import org.springframework.http.HttpStatus

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val isSuccess: Boolean,
    val code: String,
    val message: String
) {

    // Sample (추후 삭제 예정)
    FIND_SAMPLE_DATA_SUCCESS(HttpStatus.OK, true, "200", "샘플 데이터 찾기 성공!"),
    FIND_SAMPLE_DATA_LIST_SUCCESS(HttpStatus.OK, true, "200", "샘플 전체 데이터 찾기 성공!"),
    SAVE_SAMPLE_DATA_SUCCESS(HttpStatus.OK, true, "200", "샘플 데이터 저장 성공!"),

    // Authentication / Authorization
    LOGIN_SUCCESS(HttpStatus.OK, true, "200", "로그인 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, true, "200", "로그아웃 성공"),
    SIGNUP_SUCCESS(HttpStatus.CREATED, true, "201", "회원가입 성공"),
    RESIGN_SUCCESS(HttpStatus.OK, true, "200", "회원탈퇴 성공"),

    // User
    FIND_USER_INFO_SUCCESS(HttpStatus.OK, true, "200", "회원정보 조회 성공"),
    PATCH_USER_INFO_SUCCESS(HttpStatus.OK, true, "200", "회원정보 수정 성공"),
    FIND_USER_SCHEDULES_SUCCESS(HttpStatus.OK, true, "200", "회원 모임 일정 조회 성공"),
    FIND_USER_CLASSES_SUCCESS(HttpStatus.OK, true, "200", "회원 전체 모임 조회 성공"),

    // Class
    CLASS_CREATE_SUCCESS(HttpStatus.CREATED, true, "201", "모임이 생성되었습니다."),
    CLASS_JOIN_SUCCESS(HttpStatus.OK, true, "200", "모임에 가입되었습니다."),
    CLASS_RESIGN_SUCCESS(HttpStatus.OK, true, "200", "모임에서 탈퇴되었습니다."),
    CLASS_MODIFY_SUCCESS(HttpStatus.OK, true, "200", "수정 성공했습니다!"),
    CLASS_DELETE_SUCCESS(HttpStatus.OK, true, "200", "모임이 삭제되었습니다."),
    CLASS_MASTER_TRANSFER_SUCCESS(HttpStatus.OK, true, "200", "권한을 위임했습니다."),
    CLASS_KICK_OUT_SUCCESS(HttpStatus.OK, true, "200", "회원을 강퇴한 후 블랙리스트에 등록했습니다."),
    CLASS_READ_SUCCESS(HttpStatus.OK, true, "200", "모임이 조회되었습니다"),

    // Schedules
    SCHEDULE_CREATE_SUCCESS(HttpStatus.CREATED, true, "201", "모임 일정 생성되었습니다!"),
    SCHEDULE_READ_SUCCESS(HttpStatus.OK, true, "200", "모임 일정 조회되었습니다!"),
    SCHEDULE_MODIFY_SUCCESS(HttpStatus.OK, true, "200", "모임 일정이 성공적으로 수정되었습니다."),
    SCHEDULE_DELETE_SUCCESS(HttpStatus.OK, true, "200", "모임 일정이 삭제되었습니다."),

    // CheckIn
    CHECK_IN_CREATE_SUCCESS(HttpStatus.CREATED, true, "201", "모임 참가 신청 완료되었습니다!"),
    CHECK_IN_UPDATE_SUCCESS(HttpStatus.OK, true, "200", "모임 참가 변경 완료되었습니다!"),
    CHECK_IN_READ_SUCCESS(HttpStatus.OK, true, "200", "해당 모임 참석 현황이 조회되었습니다!"),

    // Common
    SUCCESS(HttpStatus.OK, true, "200", "요청 응답 성공");

}
