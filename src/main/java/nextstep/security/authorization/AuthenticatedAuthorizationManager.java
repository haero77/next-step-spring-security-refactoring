package nextstep.security.authorization;

import nextstep.security.authentication.Authentication;

public class AuthenticatedAuthorizationManager<T> implements AuthorizationManager<T> {

    @Override
    public AuthorizationDecision check(Authentication authentication, T object) {
        return new AuthorizationDecision(isGranted(authentication));
    }

    private boolean isGranted(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}
