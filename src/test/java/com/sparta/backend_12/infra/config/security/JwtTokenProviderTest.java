package com.sparta.backend_12.infra.config.security;


import com.sparta.backend_12.domain.entity.User;
import com.sparta.backend_12.domain.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;


class JwtTokenProviderTest {

    JwtTokenProvider jwtTokenProvider;


    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", "dGVzdFNlY3JldEtleVRlc3RTZWNyZXRLZXlUZXN0U2VjcmV0S2V5");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 3600000L);
    }

    @DisplayName("generateToken - 유효한 유저")
    @Test
    void generateToken_ValidUser_ReturnsJwtWithClaims() {
        // given
        User user = User.create("JIN HO", "12341234", "Mentos", Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        // when
        String token = jwtTokenProvider.generateToken(user);

        // then
        Claims claims = parseToken(token);
        assertThat(claims.get("user_id", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.getSubject()).isEqualTo("JIN HO");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isAfterOrEqualTo(new Date());

    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtTokenProvider.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Test
    @DisplayName("토큰은 표준 JWT 구조를 가져야 함")
    void token_ShouldHaveThreeParts() {
        // given
        User user = User.create("user1", "pwd", "nick", Role.USER);
        ReflectionTestUtils.setField(user, "id", 2L);

        // when
        String token = jwtTokenProvider.generateToken(user);

        // then
        assertThat(token.split("\\.")).hasSize(3); // 헤더.페이로드.서명
    }

    @Test
    @DisplayName("헤더에는 HS256 알고리즘이 명시되어야 함")
    void tokenHeader_ShouldContainHS256Algorithm() throws Exception {
        // given
        User user = User.create("user2", "pwd", "nick", Role.USER);

        // when
        String token = jwtTokenProvider.generateToken(user);
        String header = new String(Base64.getUrlDecoder().decode(token.split("\\.")[0]));

        // then
        assertThat(header).contains("\"alg\":\"HS256\"");
    }

    @Test
    @DisplayName("ADMIN 역할 사용자의 토큰 생성 검증")
    void generateToken_WithAdminRole_ShouldContainRoleClaim() {
        // given
        User admin = User.create("admin", "pwd", "nick", Role.ADMIN);
        ReflectionTestUtils.setField(admin, "id", 3L);

        // when
        String token = jwtTokenProvider.generateToken(admin);
        Claims claims = parseToken(token);

        // then
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("만료 시간은 발급 시간 + 1시간 이어야 함")
    void tokenExpiration_ShouldBeOneHourAfterIssuedAt() {
        // given
        User user = User.create("timeTest", "pwd", "nick", Role.USER);

        // when
        String token = jwtTokenProvider.generateToken(user);
        Claims claims = parseToken(token);

        // then
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        long difference = expiration.getTime() - issuedAt.getTime();

        assertThat(difference).isEqualTo(3600000L); // 1시간(ms)
    }

    @Test
    @DisplayName("동일 사용자로 생성된 여러 토큰의 클레임은 일관적이어야 함")
    void multipleTokens_ForSameUser_ShouldHaveConsistentClaims() {
        // given
        User user = User.create("multiUser", "pwd", "nick", Role.USER);
        ReflectionTestUtils.setField(user, "id", 5L);

        // when
        String token1 = jwtTokenProvider.generateToken(user);
        String token2 = jwtTokenProvider.generateToken(user);

        // then
        Claims claims1 = parseToken(token1);
        Claims claims2 = parseToken(token2);

        assertThat(claims1.get("user_id")).isEqualTo(claims2.get("user_id"));
        assertThat(claims1.getSubject()).isEqualTo(claims2.getSubject());
        assertThat(claims1.get("role")).isEqualTo(claims2.get("role"));
    }

    @DisplayName("extractUsername - 올바른 사용자명 추출")
    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        // given
        User user = User.create("testUser", "password", "nick", Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        // when
        String token = jwtTokenProvider.generateToken(user);
        String extractedUsername = jwtTokenProvider.extractUsername(token);

        // then
        assertThat(extractedUsername).isEqualTo(user.getUsername());
    }

    @DisplayName("isTokenValid - 유효한 토큰 + 일치 사용자 → true")
    @Test
    void isTokenValid_validTokenAndUser_ReturnsTrue() {
        // given
        User user = createUser("userA", 1L);
        UserDetails userDetails = new LoginUser(user);
        String validToken = jwtTokenProvider.generateToken(user);

        // when
        boolean result = jwtTokenProvider.isTokenValid(validToken, userDetails);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("isTokenValid - 유효한 토큰 + 다른 사용자 → false")
    @Test
    void isTokenValid_validTokenButDifferentUser_ReturnsFalse() {
        // given
        User userA = createUser("userA", 1L);
        User userB = createUser("userB", 2L);
        UserDetails userDetailsB = new LoginUser(userB);
        String tokenForA = jwtTokenProvider.generateToken(userA);

        // when
        boolean result = jwtTokenProvider.isTokenValid(tokenForA, userDetailsB);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("isTokenValid - 만료된 토큰 → ExpiredJwtException 발생")
    @Test
    void isTokenValid_expiredToken_ReturnsFalse() {
        // given
        User user = createUser("expiredUser", 3L);
        UserDetails userDetails = new LoginUser(user);

        String expiredToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2시간 전
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1시간 전
                .signWith(jwtTokenProvider.getSignInKey())
                .compact();

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.isTokenValid(expiredToken, userDetails))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @DisplayName("isTokenValid - 변조된 토큰 → SignatureException 발생")
    @Test
    void isTokenValid_tamperedToken_ThrowsException() {
        // given
        User user = createUser("userC", 4L);
        String validToken = jwtTokenProvider.generateToken(user);
        String tamperedToken = validToken + "tampered";
        UserDetails userDetails = new LoginUser(user);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.isTokenValid(tamperedToken, userDetails))
                .isInstanceOf(io.jsonwebtoken.SignatureException.class);
    }

    private User createUser(String username, Long id) {
        User user = User.create(username, "password", "nick", Role.USER);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}