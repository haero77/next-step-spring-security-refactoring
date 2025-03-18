package nextstep.security.config.annotation.web.configurers;

import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import nextstep.security.access.RequestMatcherEntry;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.authorization.*;
import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthorizeHttpRequestsConfigurer implements SecurityConfigurer {

    List<RequestMatcherEntry<AuthorizationManager>> mappings = new ArrayList<>();
    private List<? extends RequestMatcher> currentMatchers;
    private final RoleHierarchy roleHierarchy;

    public AuthorizeHttpRequestsConfigurer(RoleHierarchy roleHierarchy) {
        Assert.notNull(roleHierarchy, "roleHierarchy cannot be null");
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public void init(HttpSecurity http) {
        // AuthorizeHttpRequestsConfigurer.hasRole() 보다 HttpSecurity.build()가 먼저 수행되므로,
        // init()에서 roleHierarchy 초기화를 진행 시 .hasRole() 시점에 roleHierarchy가 null이 되는 문제 발생 -> 생성자에서 roleHierarchy 주입.
    }

    @Override
    public void configure(HttpSecurity http) {
        RequestMatcherDelegatingAuthorizationManager manager = new RequestMatcherDelegatingAuthorizationManager(mappings);
        AuthorizationFilter filter = new AuthorizationFilter(manager);
        http.addFilter(filter);
    }

    public AuthorizeHttpRequestsConfigurer requestMatchers(String... patterns) {
        this.currentMatchers = Arrays.stream(patterns)
                .map(pattern -> new MvcRequestMatcher(null, pattern))
                .toList();

        return this;
    }

    public AuthorizeHttpRequestsConfigurer anyRequest() {
        this.currentMatchers = List.of(AnyRequestMatcher.INSTANCE);
        return this;
    }

    public AuthorizeHttpRequestsConfigurer hasRole(String role) {
        addMappings(new AuthorityAuthorizationManager<>(this.roleHierarchy, role));
        return this;
    }

    public AuthorizeHttpRequestsConfigurer permitAll() {
        addMappings(new PermitAllAuthorizationManager<>());
        return this;
    }

    public AuthorizeHttpRequestsConfigurer authenticated() {
        addMappings(new AuthenticatedAuthorizationManager<>());
        return this;
    }

    private <T> void addMappings(AuthorizationManager<T> authorizationManager) {
        for (RequestMatcher matcher : this.currentMatchers) {
            this.mappings.add(
                    new RequestMatcherEntry<>(matcher, authorizationManager)
            );
        }
    }
}
