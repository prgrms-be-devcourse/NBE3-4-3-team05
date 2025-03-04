package z9.hobby.domain.classes.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import z9.hobby.model.BaseEntity;
import z9.hobby.model.schedules.SchedulesEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "favorite", nullable = false)
    private String favorite;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "master_id", nullable = false)
    private Long masterId;

    @OneToMany(mappedBy = "classes", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<ClassUserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "classes", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<ClassBlackListEntity> blackLists = new ArrayList<>();

    @OneToMany(mappedBy = "classes", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<SchedulesEntity> schedules = new ArrayList<>();

    public ClassUserEntity addMember(Long userId) {
        ClassUserEntity user = ClassUserEntity.builder()
                .classes(this)
                .userId(userId)
                .build();

        users.add(user);

        return user;
    }

    public void removeMember(ClassUserEntity user) {
        users.remove(user);
    }

    public void updateClassInfo(String name, String description) {
        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
    }

    public void setMasterId(Long userId) {
        masterId = userId;
    }

    public void addBlackList(Long userId) {
        ClassBlackListEntity blackUser = ClassBlackListEntity.builder()
                .classes(this)
                .userId(userId)
                .build();

        blackLists.add(blackUser);
    }
}
