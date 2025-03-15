package nextstep.security.web.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * Server-Side Rendering 시에는 HttpSessionCsrfTokenRepository 사용.
 *  -> 쿠키에 CSRF 토큰을 저장하지 않고 세션에만 저장하므로 헤더에 CSRF 토큰을 실을 수는 없음. (테스트에서는 헤더에 CSRF 토큰을 실음)
 *  -> 반면에 SSR에서는 html hidden 필드에 CSRF 토큰을  '_csrf' 파라미터에 실어서 전송할 수 있음.
 * Client-Side Rendering 시에는 CookieCsrfTokenRepository 사용.
 *  -> 쿠키에 CSRF 토큰 값을 저장해야 하므로 프론트에서 쿠키값을 읽어서 헤더에 CSRF 토큰을 실을 수 있음.
 */
public final class HttpSessionCsrfTokenRepository implements CsrfTokenRepository {

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";
    private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class.getName()
            .concat(".CSRF_TOKEN");

    private final String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;

    @Override
    public CsrfToken generateToken() {
        return new DefaultCsrfToken(DEFAULT_CSRF_HEADER_NAME, DEFAULT_CSRF_PARAMETER_NAME, createNewToken());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token != null) {
            HttpSession session = request.getSession();
            session.setAttribute(this.sessionAttributeName, token);
            return;
        }

        // when token is null, remove CSRF Token from the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(this.sessionAttributeName);
        }
    }

    @Override
    @Nullable
    public CsrfToken loadToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return (CsrfToken) session.getAttribute(this.sessionAttributeName);
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }
}
