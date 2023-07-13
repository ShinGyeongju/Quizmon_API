package kr.quizmon.api.global.SecurityFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.quizmon.api.global.Util.JwtProvider;
import kr.quizmon.api.global.Util.RedisIO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisIO redisIO;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String cookieToken = jwtProvider.resolveCookieToken(request);
            String token = cookieToken == null ? jwtProvider.resolveHeaderToken(request) : cookieToken;
            //String token = jwtProvider.resolveHeaderToken(request);

            // token 유효성 검증
            if (token != null && jwtProvider.validateToken(token) && !redisIO.hasLogoutKey(token)) {
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            // Not found user
        }

        filterChain.doFilter(request, response);
    }

}
