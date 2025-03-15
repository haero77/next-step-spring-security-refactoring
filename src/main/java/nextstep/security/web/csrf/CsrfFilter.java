package nextstep.security.web.csrf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.AccessDeniedHandler;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public final class CsrfFilter extends OncePerRequestFilter {

    public static final RequestMatcher DEFAULT_CSRF_MATCHER = new DefaultRequiresCsrfMatcher();

    private final CsrfTokenRepository tokenRepository = new HttpSessionCsrfTokenRepository();
    private final Set<MvcRequestMatcher> ignoringRequestMatchers;
    private RequestMatcher requireCsrfProtectionMatcher = DEFAULT_CSRF_MATCHER;
    private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandler();

    public CsrfFilter() {
        this(Set.of());
    }

    public CsrfFilter(Set<MvcRequestMatcher> ignoringRequestMatchers) {
        this.ignoringRequestMatchers = ignoringRequestMatchers;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return this.ignoringRequestMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        CsrfToken token = setUpToken(request, response);

        if (!this.requireCsrfProtectionMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Need to check CSRF token
        String actualToken = getActualToken(request, token);
        if (!Objects.equals(token.getToken(), actualToken)) {
            this.accessDeniedHandler.handle(response);
            return;
        }

        // CSRF token is valid
        filterChain.doFilter(request, response);
    }

    @Nullable
    private String getActualToken(HttpServletRequest request, CsrfToken token) {
        String actualToken = request.getHeader(token.getHeaderName());

        if (actualToken == null) {
            actualToken = request.getParameter(token.getParameterName());
        }

        return actualToken;
    }

    private CsrfToken setUpToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrfToken = this.tokenRepository.loadToken(request);
        if (csrfToken == null) {
            csrfToken = this.tokenRepository.generateToken();
            this.tokenRepository.saveToken(csrfToken, request, response);
        }
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);

        return csrfToken;
    }

    public void setRequireCsrfProtectionMatcher(RequestMatcher requireCsrfProtectionMatcher) {
        Assert.notNull(requireCsrfProtectionMatcher, "requireCsrfProtectionMatcher cannot be null");
        this.requireCsrfProtectionMatcher = requireCsrfProtectionMatcher;
    }

    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {

        private static final Set<String> ALLOWED_METHODS = Set.of("GET", "HEAD", "TRACE", "OPTIONS");

        @Override
        public boolean matches(HttpServletRequest request) {
            // 허용한 메서드에 포함되지 않는 메서드라면 CSRF 토큰 검증이 필요하다.
            return !ALLOWED_METHODS.contains(request.getMethod());
        }
    }
}
