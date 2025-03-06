package z9.hobby.domain.search

enum class SortBy {
    FAVORITE,  // 관심사 순
    CREATED_DESC,  // 최근 등록순
    CREATED_ASC,  // 나중 등록순
    NAME_ASC,  // 가나다 순
    PARTICIPANT_DESC // 참여인원 많은 순 (ClassUserEntity size 기준)
}
