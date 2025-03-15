package nextstep.security.web.csrf;

public interface CsrfToken {

    String getHeaderName();

    String getParameterName();

    String getToken();
}
