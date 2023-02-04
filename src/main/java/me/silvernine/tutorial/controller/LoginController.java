package me.silvernine.tutorial.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.silvernine.tutorial.entity.User;
import me.silvernine.tutorial.jwt.JwtFilter;
import me.silvernine.tutorial.jwt.TokenProvider;
import me.silvernine.tutorial.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipalNotFoundException;

/**
 * 로그인 기능을 담당하는 컨트롤러입니다.
 */
@Tag(name = "login", description = "로그인 API")
@RestController
@RequiredArgsConstructor
public class LoginController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserRepository userRepository;

//    public LoginController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
//        this.tokenProvider = tokenProvider;
//        this.authenticationManagerBuilder = authenticationManagerBuilder;
//    }

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {

//        String encryptPassword = SHA512KeyGenerator.generate(password);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(username + " -> ID 없음"));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(password, user.getPassword()) == false) {
            throw new RuntimeException(username + " -> PW 오류");
        }

        // UsernamePasswordAuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 이때 loadUserByUsername 메소드가 수행되며 유저 정보를 조회해서 인증 정보를 생성하게 됩니다.
        // 실제 pw 검사는 DaoAuthenticationProvider 에서 수행합니다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // authentication 정보를 securityContext에 set
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // authentication 정보를 기반으로 jwt 생성
        String jwt = tokenProvider.createToken(authentication);

        // response header의 에 jwt token 정보를 전달합니다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);
    }
}