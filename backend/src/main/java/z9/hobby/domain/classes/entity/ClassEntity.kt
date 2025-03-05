package z9.hobby.domain.classes.entity;

import jakarta.persistence.*
import z9.hobby.model.BaseEntity
import z9.hobby.model.schedules.SchedulesEntity

@Entity
class ClassEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "favorite", nullable = false)
    var favorite: String,

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    var description: String,

    @Column(name = "master_id", nullable = false)
    var masterId: Long?
) : BaseEntity() {
    @OneToMany(mappedBy = "classes", cascade = [CascadeType.PERSIST, CascadeType.REMOVE], orphanRemoval = true)
    val users: MutableList<ClassUserEntity> = mutableListOf()

    @OneToMany(mappedBy = "classes", cascade = [CascadeType.PERSIST, CascadeType.REMOVE], orphanRemoval = true)
    val blackLists: MutableList<ClassBlackListEntity> = mutableListOf()

    @OneToMany(mappedBy = "classes", cascade = [CascadeType.PERSIST, CascadeType.REMOVE], orphanRemoval = true)
    val schedules: MutableList<SchedulesEntity> = mutableListOf()

    fun addMember(userId: Long): ClassUserEntity {
        val user = ClassUserEntity(
            classes = this,
            userId = userId
        )

        users.add(user)

        return user
    }

    fun removeMember(user: ClassUserEntity) {
        users.remove(user)
    }

    fun updateClassInfo(name: String?, description: String?) {
        if (name != null) {
            this.name = name
        }
        if (description != null) {
            this.description = description
        }
    }

    fun addBlackList(userId: Long) {
        val blackUser = ClassBlackListEntity(
            classes = this,
            userId = userId
        )

        blackLists.add(blackUser)
    }
}
