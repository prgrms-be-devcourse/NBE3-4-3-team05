package z9.hobby.global.annotation.validation.user

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Email
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [])
@Retention(AnnotationRetention.RUNTIME)
@Email(message = "이메일 형식이 아닙니다.")
annotation class UserLoginId(
    val message: String = "Invalid user email",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)