> 미션 4: 취약점 대응 & 리팩토링

# 요구사항

## 실습 - 취약점 대응(CsrfFilter)

>  CsrfFilter를 이용한 CSRF 공격 대응

- [x] CsrfToken 구현
- [x] CsrfTokenRepository 구현 - HttpSessionCsrfTokenRepository
  - [x] CsrfToken 발급/저장/조회
- [x] CsrfFilter 구현
  - [x] CsrfTokenRepository를 이용한 CsrfToken 검증

## 1단계 - SecurityFilterChain 리팩토링

> 주요 클래스
> - HttpSecurity
> - HttpSecurityConfiguration
> - SecurityConfigurer
> - Customizer

- [ ] HttpSecurity 구현
- [ ] HttpSecurityConfiguration를 이용한 HttpSecurity 빈 등록
- 

## 2단계 - 인증 관련 리팩토링

## 3단계 - 인가 관련 리팩토링

## 4단계 - Auto Configuration 적용 
