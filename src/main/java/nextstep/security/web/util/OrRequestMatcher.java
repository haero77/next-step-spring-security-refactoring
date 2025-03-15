package nextstep.security.web.util;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;
import org.springframework.util.Assert;

import java.util.List;

public class OrRequestMatcher implements RequestMatcher {

    private final List<RequestMatcher> matchers;

    public OrRequestMatcher(List<RequestMatcher> matchers) {
        Assert.notEmpty(matchers, "matchers cannot be empty");
        this.matchers = matchers;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : matchers) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
