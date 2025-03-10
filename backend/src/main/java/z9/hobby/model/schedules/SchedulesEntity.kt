package z9.hobby.model.schedules

import jakarta.persistence.*
import z9.hobby.domain.classes.entity.ClassEntity
import z9.hobby.model.BaseEntity
import z9.hobby.model.checkIn.CheckInEntity

@Entity
@Table(name = "schedules")
class SchedulesEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedules_id", nullable = false)
    private val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private val classes: ClassEntity? = null,

    @Column(name = "meeting_time", nullable = false)
    private var meetingTime: String? = null,

    @Column(name = "meeting_title", nullable = false)
    private var meetingTitle: String? = null,

    @Column(name = "meeting_place", nullable = false)
    private var meetingPlace: String? = null,

    @Column(name = "latitude", nullable = false)
    private var latitude: Double? = null,

    @Column(name = "longitude", nullable = false)
    private var longitude: Double? = null,

    @OneToMany(mappedBy = "schedules", cascade = [CascadeType.ALL], orphanRemoval = true)
    private var checkins: MutableList<CheckInEntity> = mutableListOf()
) : BaseEntity() {
    // Getters
    fun getId(): Long? = id
    fun getClasses(): ClassEntity = classes ?: throw IllegalStateException("Classes should not be null")
    fun getMeetingTime(): String? = meetingTime
    fun getMeetingTitle(): String? = meetingTitle
    fun getMeetingPlace(): String? = meetingPlace
    fun getLatitude(): Double? = latitude
    fun getLongitude(): Double? = longitude
    fun getCheckins(): List<CheckInEntity> = checkins

    fun updateSchedule(meetingTime: String?, meetingTitle: String?, meetingPlace: String?, latitude: Double?, longitude: Double?) {
        this.meetingTime = meetingTime
        this.meetingTitle = meetingTitle
        this.meetingPlace = meetingPlace
        this.latitude = latitude
        this.longitude = longitude
    }
    // Builder pattern implementation in Kotlin
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private var id: Long? = null
        private var classes: ClassEntity? = null
        private var meetingTime: String? = null
        private var meetingTitle: String? = null
        private var meetingPlace: String? = null
        private var latitude: Double? = null
        private var longitude: Double? = null
        private var checkins: MutableList<CheckInEntity> = mutableListOf()

        fun id(id: Long?) = apply { this.id = id }
        fun classes(classes: ClassEntity?) = apply { this.classes = classes }
        fun meetingTime(meetingTime: String?) = apply { this.meetingTime = meetingTime }
        fun meetingTitle(meetingTitle: String?) = apply { this.meetingTitle = meetingTitle }
        fun meetingPlace(meetingPlace: String?) = apply { this.meetingPlace = meetingPlace }
        fun latitude(latitude: Double?) = apply { this.latitude = latitude }
        fun longitude(longitude: Double?) = apply { this.longitude = longitude }
        fun checkins(checkins: List<CheckInEntity>) = apply { this.checkins.addAll(checkins) }

        fun build() = SchedulesEntity(
            id = id,
            classes = classes,
            meetingTime = meetingTime,
            meetingTitle = meetingTitle,
            meetingPlace = meetingPlace,
            latitude = latitude,
            longitude = longitude,
            checkins = checkins
        )
    }
}