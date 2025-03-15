package nextstep.security.access;

import jakarta.servlet.http.HttpServletResponse;

public class AccessDeniedHandler {

    public void handle(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
