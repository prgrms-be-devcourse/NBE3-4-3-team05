package z9.hobby

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HobbyApplication

fun main(args: Array<String>) {
    runApplication<HobbyApplication>(*args)
}