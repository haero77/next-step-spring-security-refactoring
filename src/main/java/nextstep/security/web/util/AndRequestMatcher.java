package nextstep.security.web.util;

import jakarta.servlet.http.HttpServletRequest;
import nextstep.security.access.RequestMatcher;
import org.springframework.util.Assert;

import java.util.List;

public class AndRequestMatcher implements RequestMatcher {

    private final List<RequestMatcher> requestMatchers;

    public AndRequestMatcher(RequestMatcher... requestMatchers) {
        this(List.of(requestMatchers));
    }

    public AndRequestMatcher(List<RequestMatcher> requestMatchers) {
        Assert.notEmpty(requestMatchers, "requestMatchers cannot be empty");
        this.requestMatchers = requestMatchers;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher requestMatcher : this.requestMatchers) {
            if (!requestMatcher.matches(request)) {
                return false;
            }
        }
        return true;
    }
}
