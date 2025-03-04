package z9.hobby.global.annotation.validation.user

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Size(min = 8, max = 20, message = "비밀번호는 8자리 ~ 20자리 사이 입니다.")
@Pattern(regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
    message = "비밀번호는 특수문자를 반드시 포함하여야 합니다.")
annotation class UserPassword(
    val message: String = "Invalid user password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
