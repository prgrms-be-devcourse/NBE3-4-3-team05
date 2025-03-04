package z9.hobby.domain.authentication.service;

import z9.hobby.domain.authentication.dto.AuthenticationRequest;
import z9.hobby.domain.authentication.dto.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse.UserToken login(AuthenticationRequest.Login dto);

    AuthenticationResponse.UserToken oauthLogin(String provider, String authCode);

    void signup(AuthenticationRequest.Signup signupDto);

    void logout(String userId);

    void resign(String userId);
}
