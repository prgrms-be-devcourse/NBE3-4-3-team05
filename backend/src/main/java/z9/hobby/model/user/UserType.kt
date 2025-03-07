package z9.hobby.model.user

enum class UserType(val value: String) {
    NORMAL("일반 회원"),
    OAUTH("소셜 회원")
}