package com.example.config;

import com.example.member.MemberSecurityService;
import com.example.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final MemberSecurityService memberSecurityService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/member/login")
                                .defaultSuccessUrl("/member?msg=%s".formatted(Ut.url.encode("환영합니다!")))
                )
                //로그인 설정
                .authorizeRequests(
                        authorizeRequests -> authorizeRequests
                                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                .hasAuthority("ADMIN")
                                .anyRequest()
                                .permitAll()
                )
                //로그아웃 설정
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .logoutSuccessUrl("/member/login")
                .invalidateHttpSession(true)
        ;
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}