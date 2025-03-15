package nextstep.security.web.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CsrfTokenRepository {

    CsrfToken generateToken();

    void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response);

    CsrfToken loadToken(HttpServletRequest request);
}
