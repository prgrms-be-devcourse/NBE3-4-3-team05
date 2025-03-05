package z9.hobby.model.checkIn

import jakarta.persistence.*
import z9.hobby.model.BaseEntity
import z9.hobby.model.schedules.SchedulesEntity

@Entity
@Table(name = "schedules_checkin")
data class CheckInEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sc_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedules_id")
    val schedules: SchedulesEntity? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "check_in", nullable = false)
    val checkIn: Boolean
) : BaseEntity()

