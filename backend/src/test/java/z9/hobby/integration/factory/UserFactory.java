package z9.hobby.integration.factory;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRepository;
import z9.hobby.model.userfavorite.UserFavorite;
import z9.hobby.model.userfavorite.UserFavoriteRepository;

@Component
@RequiredArgsConstructor
public final class UserFactory {

    public static final String USER_LOGIN_ID_PREFIX = "test";
    public static final String USER_LOGIN_ID_END = "@email.com";
    public static final String USER_LOGIN_PASSWORD = "!test1234";
    public static final String USER_LOGIN_NICKNAME_PREFIX = "TEST";

    private final EntityManager em;
    private final UserRepository userRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * count :  생성할 회원 수량. 1부터 시작
     *          0일 경우, 생성하지 않음.
     */
    public List<User> saveAndCreateUserData(final int count) {
        if(count == 0) return List.of();

        ArrayList<User> savedUserList = new ArrayList<>(count);

        for(int index=1; index<=count; index++) {
            String loginId = String.format("%s%d%s", USER_LOGIN_ID_PREFIX, index, USER_LOGIN_ID_END);
            String password = passwordEncoder.encode(USER_LOGIN_PASSWORD);
            String nickname = String.format("%s%d", USER_LOGIN_NICKNAME_PREFIX, index);

            User newUser = User.createNewUser(loginId, password, nickname);
            User saveUser = userRepository.save(newUser);

            savedUserList.add(saveUser);
        }

        flushAndClear();

        return savedUserList;
    }

    public void saveUserFavorite(User user, List<FavoriteEntity> favoriteList) {
        for (FavoriteEntity favorite : favoriteList) {
            userFavoriteRepository.save(UserFavorite.createNewUserFavorite(user, favorite));
        }
        flushAndClear();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
