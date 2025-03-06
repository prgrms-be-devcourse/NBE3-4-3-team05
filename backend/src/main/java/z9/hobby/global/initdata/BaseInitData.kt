package z9.hobby.global.initdata

import lombok.RequiredArgsConstructor
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.domain.classes.entity.ClassUserEntity
import z9.hobby.domain.classes.repository.ClassRepository
import z9.hobby.domain.classes.repository.ClassUserRepository
import z9.hobby.domain.favorite.entity.FavoriteEntity
import z9.hobby.domain.favorite.repository.FavoriteRepository
import z9.hobby.model.sample.SampleEntity
import z9.hobby.model.sample.SampleRepository
import z9.hobby.model.schedules.SchedulesEntity
import z9.hobby.model.schedules.SchedulesRepository
import z9.hobby.model.user.User
import z9.hobby.model.user.UserRepository
import z9.hobby.model.userfavorite.UserFavorite
import z9.hobby.model.userfavorite.UserFavoriteRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Profile("dev")
@Component
@RequiredArgsConstructor
class BaseInitData(
    private val sampleRepository: SampleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val favoriteRepository: FavoriteRepository,
    private val userFavoriteRepository: UserFavoriteRepository,
    private val classRepository: ClassRepository,
    private val classUserRepository: ClassUserRepository,
    private val schedulesRepository: SchedulesRepository
) {
    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun init() {
        val sampleData = createSampleData(10)
        val saveFavoriteData = createFavoriteData() // 먼저 관심사 생성
        val savedUserData = createUserData(10)
        val savedClassData = createClassData(10, savedUserData!!)
        createScheduleData(savedClassData)
        val savedUserFavoriteData = createUserFavoriteData(savedUserData, saveFavoriteData)
    }

    private fun createUserFavoriteData(
        savedUserData: List<User>,
        saveFavoriteData: List<FavoriteEntity>
    ): List<UserFavorite> {
        val savedUserFavoriteData: MutableList<UserFavorite> = ArrayList()

        for (savedUser in savedUserData) {
            for (savedFavorite in saveFavoriteData) {
                val newUserFavorite = UserFavorite.createNewUserFavorite(savedUser, savedFavorite)
                val save = userFavoriteRepository.save(newUserFavorite)
                savedUserFavoriteData.add(save)
            }
        }

        return savedUserFavoriteData
    }

    private fun createSampleData(count: Int): List<SampleEntity>? {
        if (sampleRepository.count() != 0L) {
            return sampleRepository.findAll()
        }
        if (count == 0) {
            return null
        }

        val savedDataList: MutableList<SampleEntity> = ArrayList()
        for (i in 1..count) {
            val firstName = "김"
            val secondName = String.format("%s%d", "아무개", i)
            val sample = SampleEntity.builder().firstName(firstName).secondName(secondName)
                .age(i).build()
            savedDataList.add(sampleRepository.save(sample))
        }

        return savedDataList
    }

    private fun createUserData(count: Int): List<User>? {
        if (userRepository.count() != 0L) {
            return userRepository.findAll()
        }
        if (count == 0) {
            return null
        }

        val savedUserList: MutableList<User> = ArrayList()
        for (i in 1..count) {
            val loginId = String.format("%s%d@email.com", "test", i)
            val password = passwordEncoder.encode("!test1234")
            val nickname = String.format("%s%d", "test", i)
            savedUserList.add(
                userRepository.save(
                    User.createNewUser(loginId, password, nickname)
                )
            )
        }

        return savedUserList
    }

    private fun createFavoriteData(): List<FavoriteEntity> {
        if (favoriteRepository.count() != 0L) {
            return favoriteRepository.findAll()
        }

        val savedFavoriteList: MutableList<FavoriteEntity> = ArrayList()

        val favorite1 = favoriteRepository.save(FavoriteEntity.createNewFavorite("축구"))
        val favorite2 = favoriteRepository.save(FavoriteEntity.createNewFavorite("영화"))
        val favorite3 = favoriteRepository.save(FavoriteEntity.createNewFavorite("독서"))
        val favorite4 = favoriteRepository.save(FavoriteEntity.createNewFavorite("그림"))
        val favorite5 = favoriteRepository.save(FavoriteEntity.createNewFavorite("코딩"))
        val favorite6 = favoriteRepository.save(FavoriteEntity.createNewFavorite("음악"))

        savedFavoriteList.addAll(java.util.List.of(favorite1, favorite2, favorite3, favorite4, favorite5, favorite6))

        return savedFavoriteList
    }

    private fun createClassData(count: Int, users: List<User>): List<ClassEntity> {
        if (classRepository.count() != 0L) {
            return classRepository.findAll()
        }

        // 먼저 저장된 관심사 목록을 가져옵니다
        val favorites = favoriteRepository.findAll()
        val savedClassList: MutableList<ClassEntity> = ArrayList()

        for (i in 1..count) {
            // 각 클래스의 모임장을 users 리스트에서 순차적으로 설정
            val masterId = users[i - 1].id

            // favorites 리스트에서 순환하면서 관심사를 선택 (인덱스가 넘어가면 처음부터 다시)
            val favorite = favorites[(i - 1) % favorites.size].name

            // 코틀린 생성자 방식 사용
            val classEntity = ClassEntity(
                name = "테스트 모임$i",
                favorite = favorite,
                description = "테스트 모임${i}의 설명입니다.",
                masterId = masterId
            )

            val savedClass = classRepository.save(classEntity)
            savedClassList.add(savedClass)

            // 모임장을 ClassUser로 추가
            val classUser = ClassUserEntity(
                classes = savedClass,
                userId = masterId
            )
            classUserRepository.save(classUser)
        }

        return savedClassList
    }

    private fun createScheduleData(classes: List<ClassEntity>) {
        // 이미 데이터가 있으면 중단
        if (schedulesRepository.count() > 0) return

        classes.forEach { classEntity ->
            // 각 클래스마다 3개의 일정 생성
            (1..3).forEach { i ->
                // 현재 시간으로부터 i주 후로 설정
                val futureTime = LocalDate.now().plusWeeks(i.toLong())
                val meetingTime = futureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                val schedule = SchedulesEntity.builder()
                    .classes(classEntity)
                    .meetingTime(meetingTime)
                    .meetingTitle("모임 ${classEntity.id}의 ${i}번째 일정")
                    .build()

                schedulesRepository.save(schedule)
            }
        }
    }
}