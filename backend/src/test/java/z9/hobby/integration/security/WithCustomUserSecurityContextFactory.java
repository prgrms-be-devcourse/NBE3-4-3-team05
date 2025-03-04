package z9.hobby.integration.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import z9.hobby.global.security.user.CustomUserDetails;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRole;

public class WithCustomUserSecurityContextFactory implements
        WithSecurityContextFactory<WithCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.createSecurityContextUser(
                Long.parseLong(annotation.username()),
                UserRole.valueOf(annotation.role()));

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
        context.setAuthentication(auth);

        return context;

    }
}
