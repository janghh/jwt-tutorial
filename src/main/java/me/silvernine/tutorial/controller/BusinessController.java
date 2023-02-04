package me.silvernine.tutorial.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.silvernine.tutorial.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * JWT 토큰 검증이 정상적으로 동작하는지 테스트하기 위한 기본적인 API 목록입니다.
 */
@Tag(name = "test", description = "테스트 API")
@RestController
public class BusinessController {

    /**
     * 인증 없이 허용되는 api
     */
    @GetMapping("/guest")
    public ResponseEntity<String> guest() {
        return ResponseEntity.ok("인증없이 허용되는 기능");
    }

    /**
     * 관리자, 사용자에게 허용되는 API
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> user() {
        return ResponseEntity.ok("관리자, 사용자에게 허용되는 기능");
    }

    /**
     * ADMIN 에게만 허용되는 API
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("관리자에게만 허용되는 기능");
    }
}