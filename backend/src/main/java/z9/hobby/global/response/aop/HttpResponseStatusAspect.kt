package z9.hobby.global.response.aop

import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import z9.hobby.global.response.BaseResponse

@Aspect
@Component
@RequiredArgsConstructor
class HttpResponseStatusAspect(
    private val response: HttpServletResponse
) {
    @Around("""
        (
            within(
                @org.springframework.web.bind.annotation.RestController *
            )
            &&
            (
                @annotation(org.springframework.web.bind.annotation.GetMapping)
                ||
                @annotation(org.springframework.web.bind.annotation.PostMapping)
                ||
                @annotation(org.springframework.web.bind.annotation.PutMapping)
                ||
                @annotation(org.springframework.web.bind.annotation.DeleteMapping)
                ||
                @annotation(org.springframework.web.bind.annotation.RequestMapping)
            )
        )
        ||
        @annotation(org.springframework.web.bind.annotation.ResponseBody)
    """)
    @Throws(Throwable::class)
    fun handleResponse(joinPoint: ProceedingJoinPoint): Any? {
        val proceed = joinPoint.proceed()

        if (proceed is BaseResponse<*>) {
            response.status = proceed.httpStatus.value()
        }

        return proceed
    }
}
