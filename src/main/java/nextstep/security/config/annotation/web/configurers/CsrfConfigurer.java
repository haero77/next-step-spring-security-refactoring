package nextstep.security.config.annotation.web.configurers;

import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import nextstep.security.web.csrf.CsrfFilter;
import nextstep.security.web.util.AndRequestMatcher;
import nextstep.security.web.util.NegatedRequestMatcher;
import nextstep.security.web.util.OrRequestMatcher;

import java.util.ArrayList;
import java.util.List;

public class CsrfConfigurer implements SecurityConfigurer {

    private RequestMatcher csrfRequiredRequestMatcher;
    private List<RequestMatcher> ignoredCsrfProtectionMatchers;

    @Override
    public void init(HttpSecurity http) {
        this.csrfRequiredRequestMatcher = CsrfFilter.DEFAULT_CSRF_MATCHER;
        this.ignoredCsrfProtectionMatchers = new ArrayList<>();
    }

    @Override
    public void configure(HttpSecurity http) {
        CsrfFilter filter = new CsrfFilter();

        RequestMatcher csrfProtectionRequired = getCsrfProtectionRequiredRequestMatcher();
        filter.setRequireCsrfProtectionMatcher(csrfProtectionRequired);

        http.addFilter(filter);
    }

    private RequestMatcher getCsrfProtectionRequiredRequestMatcher() {
        if (this.ignoredCsrfProtectionMatchers.isEmpty()) {
            return this.csrfRequiredRequestMatcher;
        }

        return new AndRequestMatcher(
                this.csrfRequiredRequestMatcher,
                new NegatedRequestMatcher(new OrRequestMatcher(this.ignoredCsrfProtectionMatchers))
        );
    }

    public void ignoringRequestMatchers(String... patterns) {
        for (String pattern : patterns) {
            MvcRequestMatcher mvc = new MvcRequestMatcher(null, pattern);
            ignoredCsrfProtectionMatchers.add(mvc);
        }
    }
}
