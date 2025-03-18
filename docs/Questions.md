> PR 코멘트로 질문 드리겠습니다..! 🙇‍♂️

- [ ] SSR에서는 HttpSessionCsrfTokenRepository, CSR에서는 CookieCsrfTokenRepository를 사용해야할 것 같은데, 그 이유를 얘기하고 근거가 적절한지 질문.

### 인증 필터간 순서

- 현재 구현한 HttpSecurity의 경우, 어떤 인증 메서드(`csrf()`, `oauth2Login()` 등)를 호출하냐에따라 SecurityFilterChain의 필터 순서가 결정된다.
- 이 방식은 `authorizeHttpRequests()` 가 다른 인증 메서드보다 먼저 호출될 경우, `AuthorizationFilter`가 다른 인증 필터보다 먼저 수행하기 때문에 인증 전에 인가가 수행되버리는 문제가 있다.
  - `authorizeHttpRequests()` 가 먼저 호출되어도 필터 체인의 가장 마지막에 순서를 위치시키도록 하는 로직이 필요함. (우선 강제로 순서를 맞춰주는 방식으로 구현)

### authorizationFlter에서는 403을 리턴하는데 인증되지 않은 경우는 401을 리턴해야한다.

- 문제를 해결하기 위해 exceptionTranslationFilter를 지금 구현하는 것은 오버 엔지니어링.

### RoleHierarchy 가 빈으로 등록되었는데, 찾지 못하는 문제?

- httpSecurity.build 보다 hasRole이 먼저 호출되고, 그래서 this.roleHierarchy 가 null이 되는 문제 발생
