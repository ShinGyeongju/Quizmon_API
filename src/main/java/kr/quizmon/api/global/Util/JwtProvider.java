package kr.quizmon.api.global.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.quizmon.api.global.config.CustomConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final CustomConfig customConfig;
    private final UserDetailsService userDetailsService;

    private long expirationHour;
    private Key secretKey;
    private String headerName;

    @PostConstruct
    protected void init() {
        expirationHour = 1000L * 60 * 60 * customConfig.getJwt_expiration_hour();
        this.secretKey = Keys.hmacShaKeyFor(customConfig.getJwt_secret_key().getBytes(StandardCharsets.UTF_8));
        headerName = customConfig.getJwt_header();
    }

    // JWT 토큰 생성
    public String createToken(String id, String role) {
        Claims claims = Jwts.claims().setSubject(id);
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationHour))
                .signWith(this.secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) throws UsernameNotFoundException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // JWT 토큰에서 회원 ID 조회
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    // JWT 토큰에서 유효 기간 조회
    public Date getExpiration(String token) {
        return Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token).getBody().getExpiration();
    }

    // Request Header에서 JWT 토큰 조회
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(headerName);
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
