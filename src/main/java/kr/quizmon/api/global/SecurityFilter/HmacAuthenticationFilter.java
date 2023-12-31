package kr.quizmon.api.global.SecurityFilter;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.quizmon.api.global.Util.HmacProvider;
import kr.quizmon.api.global.config.CustomConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class HmacAuthenticationFilter extends OncePerRequestFilter {
    final private CustomConfig customConfig;
    final private HmacProvider hmacProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String hmac = request.getHeader(customConfig.getHmac_header());

        // hmac 유효성 검증
        if (hmac == null || !validateHmac(request, hmac)) {
            // 에러 응답 전송
            sendErrorResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateHmac(HttpServletRequest request, String hmac) {
        try {
            String[] splitHmac = hmac.split(":");

            long currentEpochTime = System.currentTimeMillis();

            // Minute to millisecond
            long expiration = (long)customConfig.getHmac_expiration_minute() * 60 * 1000;

            // 유효 기간 검증
            if ((currentEpochTime - Long.parseLong(splitHmac[0])) > expiration) {
                return false;
            }

            // Hmac 생성
            String hmacMessage = splitHmac[0] + request.getMethod() + request.getRequestURI();
            String encodedHash = hmacProvider.genHmacBase64Code(customConfig.getHmac_secret_key(), hmacMessage);

            // Hmac 검증
            if (!encodedHash.equals(splitHmac[1])) {
                return false;
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
        response.setStatus(401);
        response.setContentType("application/json");

        try {
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse()));
        } catch (Exception ex) {
            // Mapping failed
            System.out.println(ex.getMessage());
        }
    }

    @NoArgsConstructor
    @Getter
    static class ErrorResponse {
        final private int code = 9000;
        final private String message = "인증에 실패했습니다.";
        final private String result = null;
    }
}
