package nextstep.security.config.annotation.web.configurers;

import nextstep.security.config.annotation.web.SecurityConfigurer;
import nextstep.security.config.annotation.web.builders.HttpSecurity;
import nextstep.security.context.SecurityContextHolderFilter;

public class SecurityContextConfigurer implements SecurityConfigurer {

    @Override
    public void init(HttpSecurity http) {
    }

    @Override
    public void configure(HttpSecurity http) {
        SecurityContextHolderFilter filter = new SecurityContextHolderFilter();
        http.addFilter(filter);
    }
}
