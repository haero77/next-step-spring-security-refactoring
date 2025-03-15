package nextstep.security.web.util;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;

public class NegatedRequestMatcher implements RequestMatcher {

    private final RequestMatcher matcher;

    public NegatedRequestMatcher(RequestMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !matcher.matches(request);
    }
}
