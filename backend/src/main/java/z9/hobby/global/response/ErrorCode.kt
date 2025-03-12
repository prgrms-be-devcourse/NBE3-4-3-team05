package z9.hobby.global.response

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val isSuccess: Boolean,
    val code: String,
    val message: String
) {

    //0000 ~ 0001
    // 오류 종류 : 샘플 도메인 에러 (추후 삭제 예정)
    NOT_EXIST_SAMPLE_DATA(HttpStatus.BAD_REQUEST, false, "0001", "샘플 데이터를 찾을 수 없습니다."),

    //1000 ~ 1999
    // 오류 종류 : 인증/인가 에러 ex) token expired
    LOGIN_FAIL(HttpStatus.BAD_REQUEST, false, "1000", "잘못된 이메일 혹은 패스워드 입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, false, "1001", "exception error 메세지에 따름"),
    NEED_LOGIN(HttpStatus.UNAUTHORIZED, false, "1002", "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, false, "1003", "접근 권한이 부족합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, false, "1004", "토큰이 만료되었습니다. 재로그인 진행해 주세요."),

    //    OAUTH_USER_LOGIN_FAIL(HttpStatus.BAD_REQUEST, false, 1005, "소셜 로그인 회원 입니다. 소셜 로그인으로 진행 해 주세요."), //NotUse
    INVALID_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, false, "1006", "지원하지 않는 로그인 방식 입니다."),
    NOT_EXIST_FAVORITE(HttpStatus.BAD_REQUEST, false, "1007", "등록되지 않은 관심사 입니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, false, "1008", "중복된 로그인 아이디 입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, false, "1009", "중복된 닉네임 입니다."),

    //2000 ~ 2999
    // 오류 종류 : 회원 도메인 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, false, "2000", "로그인 된 회원 정보 조회 실패. 재로그인 해주세요."),
    ALREADY_DELETE_USER(HttpStatus.BAD_REQUEST, false, "2001", "이미 탈퇴된 회원입니다."),
    LOGIN_RESIGN_USER(HttpStatus.BAD_REQUEST, false, "2002", "탈퇴된 회원 입니다."),

    //3000 ~ 3999
    // 오류 종류 : 모임
    CLASS_CREATE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, false, "3000", "더 이상 모임을 생성하실 수 없습니다."),
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, false, "3001", "해당하는 모임을 찾을 수 없습니다."),
    CLASS_ACCESS_DENIED(HttpStatus.FORBIDDEN, false, "3002", "해당 모임에 대한 권한이 없습니다."),
    CLASS_EXISTS_MEMBER(HttpStatus.BAD_REQUEST, false, "3003", "이미 가입된 회원입니다."),
    CLASS_NOT_EXISTS_MEMBER(HttpStatus.NOT_FOUND, false, "3003", "모임에 가입된 회원이 아닙니다."),
    CLASS_MASTER_TRANSFER_REQUIRED(HttpStatus.FORBIDDEN, false, "3004", "마스터는 권한을 위임해야만 탈퇴할 수 있습니다."),
    CLASS_MODIFY_DENIED(HttpStatus.FORBIDDEN, false, "3005", "모임방의 정보는 모임장만 수정 가능합니다."),
    CLASS_USER_FORBIDDEN(HttpStatus.FORBIDDEN, false, "3006", "권한이 없습니다."),
    CLASS_DELETE_DENIED_WITH_MEMBERS(
        HttpStatus.BAD_REQUEST,
        false,
        "3007",
        "회원이 존재하는 모임은 삭제할 수 없습니다. 권한을 위임하고 탈퇴해주세요."
    ),
    CLASS_USER_BANNED(HttpStatus.FORBIDDEN, false, "3008", "강퇴당한 회원은 재가입 하실 수 없습니다."),
    CLASS_READ_FAILED(HttpStatus.BAD_REQUEST, false, "3009", "해당하는 모임을 조회할 수 없습니다."),
    CLASS_MASTER_TRANSFER_ME(HttpStatus.BAD_REQUEST, false, "3010", "본인에게 권한을 위임할 수 없습니다."),

    //4000 ~ 4999
    // 오류 종류 : 일정
    SCHEDULE_CREATE_FAILED(HttpStatus.BAD_REQUEST, false, "4001", "일정 생성에 실패했습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, false, "4002", "해당 일정을 찾을 수 없습니다."),
    SCHEDULE_UPDATE_FAILED(HttpStatus.BAD_REQUEST, false, "4003", "일정 수정에 실패했습니다."),
    SCHEDULE_DELETE_FAILED(HttpStatus.BAD_REQUEST, false, "4004", "일정 삭제에 실패했습니다."),
    SCHEDULE_READ_FAILED(HttpStatus.BAD_REQUEST, false, "4005", "일정 조회에 실패했습니다"),
    INVALID_MEETING_TIME(HttpStatus.BAD_REQUEST, false, "4005", "과거 날짜는 설정할 수 없습니다."),
    INVALID_MEETING_TIME_FORMAT(HttpStatus.BAD_REQUEST, false, "4006", "날짜 형식이 올바르지 않습니다."),
    INVALID_DATA_RANGE(HttpStatus.BAD_REQUEST, false, "4007", "요청된 날짜가 허용된 범위를 초과했습니다."),
    INVALID_FILTER_TYPE(HttpStatus.BAD_REQUEST, false, "4008", "요청된 필터가 허용된 범위를 초과했습니다."),

    //5000 ~ 5999
    // 오류 종류 : 체크인
    CHECK_IN_CREATE_FAILED(HttpStatus.BAD_REQUEST, false, "5001", "참석 여부 생성에 실패했습니다."),
    CHECK_IN_UPDATE_FAILED(HttpStatus.BAD_REQUEST, false, "5002", "참석 여부 변경에 실패했습니다."),
    CHECK_IN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, false, "5003", "이미 참석 여부생성이 되었습니다."),
    INVALID_PASSED_CHECK_IN(HttpStatus.BAD_REQUEST, false, "5004", "마감된 일정입니다!"),
    CHECK_IN_NOT_FOUND(HttpStatus.BAD_REQUEST, false, "5005", "참석 여부 정보가 없습니다."),

    //9000 ~ 9999
    //오류 종류 : 공통 에러
    VALIDATION_FAIL_ERROR(HttpStatus.BAD_REQUEST, false, "9000", "(exception error 메세지에 따름)"),
    NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, false, "9001", "(exception error 메세지에 따름"),
    NOT_FOUND_URL(HttpStatus.NOT_FOUND, false, "9002", "요청하신 URL 을 찾을 수 없습니다."),
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, false, "9003", "데이터 저장 실패, 재시도 혹은 관리자에게 문의해주세요."),
    FAIL(HttpStatus.BAD_REQUEST, false, "9999", "요청 응답 실패, 관리자에게 문의해주세요."),
    ;
}
