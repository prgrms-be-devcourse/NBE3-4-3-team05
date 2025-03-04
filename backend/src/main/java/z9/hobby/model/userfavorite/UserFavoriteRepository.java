package z9.hobby.model.userfavorite;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    void deleteByUserId(Long userId);

    @Query("SELECT f.name FROM UserFavorite uf JOIN uf.favorite f WHERE uf.user.id = :userId")
    List<String> findFavoriteNamesByUserId(@Param("userId") Long userId);
}
