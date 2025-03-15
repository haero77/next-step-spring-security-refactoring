package nextstep.security.web.csrf;

import org.springframework.util.Assert;

public class DefaultCsrfToken implements CsrfToken {

    private final String headerName;
    private final String parameterName;
    private final String token;

    public DefaultCsrfToken(String headerName, String parameterName, String token) {
        Assert.hasText(headerName, "headerName cannot be empty");
        Assert.hasText(parameterName, "parameterName cannot be empty");
        Assert.hasText(token, "token cannot be empty");

        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }

    @Override
    public String getHeaderName() {
        return this.headerName;
    }

    @Override
    public String getParameterName() {
        return this.parameterName;
    }

    @Override
    public String getToken() {
        return this.token;
    }
}
