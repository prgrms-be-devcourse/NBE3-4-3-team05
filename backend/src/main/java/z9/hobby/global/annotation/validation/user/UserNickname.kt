package z9.hobby.global.annotation.validation.user

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Size(min = 3, max = 10, message = "닉네임은 3자리 이상 10자리 미만 입니다.")
annotation class UserNickname(
    val message: String = "Invalid user password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
