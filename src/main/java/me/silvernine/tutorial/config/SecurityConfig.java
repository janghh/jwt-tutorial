package me.silvernine.tutorial.config;

import me.silvernine.tutorial.jwt.JwtSecurityConfig;
import me.silvernine.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 관련 설정
 */
//@EnableGlobalMethodSecurity(prePostEnabled = true) // 메소드 단위로 @PreAuthorize 검증 어노테이션을 사용하기 위해 추가합니다.
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;

    /**
     * authenticationManagerBuilder.getObject().authenticate()
     * 에서 필요로 합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public SecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

//    public static String[] resources = {
//            "/", "/#/**",
//            "/v2/api-docs", "/swagger-resources/**",
//            "/swagger-ui.html", "/webjars/**", "/swagger/**", "/h2console/**",
//            "/img/**", "/css/**", "/js/**", "/env/**", "/fonts/**"
//    };

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
//                .antMatchers(resources);
                .antMatchers(
                        "/h2-console/**"
//                        , "/swagger-ui.html"
                        , "/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

//                .exceptionHandling()
//                .authenticationEntryPoint()

                // enable h2-console
//                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/error").permitAll()
                .antMatchers("/guest").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/api-docs/**", "/swagger*/**").permitAll()

                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
                ;
    }
}