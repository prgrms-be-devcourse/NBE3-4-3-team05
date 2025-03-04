package z9.hobby.model.sample;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import z9.hobby.model.BaseEntity;

@Entity
@Table(name = "sample")
@Getter
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SampleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_id", nullable = false)
    private Long id;

    @Column(name = "first_name", length = 20, nullable = false)
    private String firstName;

    @Column(name = "second_name", length = 20, nullable = false)
    private String secondName;

    @Column(name = "age", nullable = false, columnDefinition = "INTEGER CHECK (age > 0)")
    private Integer age;
}
