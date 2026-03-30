package com.halo.eventer.global.security.provider;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.halo.eventer.global.error.ErrorCode;
import com.halo.eventer.global.error.exception.BaseException;
import com.halo.eventer.global.security.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    private Key key;

    public final CustomUserDetailsService customUserDetailService;

    @Autowired
    public JwtProvider(CustomUserDetailsService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String createToken(String account, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(account);
        claims.put("roles", roles);
        Date now = new Date();
        long validityMs = Duration.ofDays(7).toMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityMs)) // 만료기간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getAccount(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object raw = claims.get("roles");
        String role;

        if (raw instanceof List<?> list) {
            if (list.isEmpty()) throw new BaseException(ErrorCode.UN_AUTHENTICATED);
            role = String.valueOf(list.get(0)).trim();
        } else if (raw instanceof String s) {
            role = s.trim();
        } else {
            throw new BaseException(ErrorCode.UN_AUTHENTICATED);
        }

        if (role.isEmpty()) {
            throw new BaseException(ErrorCode.UN_AUTHENTICATED);
        }
        return role;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            // Bearer 검증
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            // 만료되었을 시 false
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Authorization Header를 통해 인증을 한다. 헤더에서 Authorization 부분만 파싱해서 사용하기 위해 만들었음
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    //    // 토큰 검증
    //    public boolean validateToken(String token) {
    //        try {
    ////            // Bearer 검증
    ////            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
    ////                return false;
    ////            } else {
    ////                token = token.split(" ")[1].trim();
    ////            }
    //            Jws<Claims> claims =
    // Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    //            // 만료되었을 시 false
    //            return !claims.getBody().getExpiration().before(new Date());
    //        } catch (Exception e) {
    //            return false;
    //        }
    //    }

    // 쿠키 통해 인증
    //    public String resolveToken(HttpServletRequest request) {
    //        Cookie[] cookies = request.getCookies();
    //        if (cookies != null) {
    //            for (Cookie cookie : cookies) {
    //                if ("JWT".equals(cookie.getName())) {
    //                    return cookie.getValue();
    //                }
    //            }
    //        }
    //        return null;
    //    }

    // 권한정보 획득
    // Spring Security 인증과정에서 권한확인을 위한 기능
    public Authentication getAuthentication(String token) {
        String role = this.getRole(token);
        String account = this.getAccount(token);
        UserDetails userDetails;

        if (role.equals("STAMP")) {
            // STAMP 레거시: uuid로 조회 (하위 호환)
            userDetails = customUserDetailService.loadUserByUuid(account);
        } else if (role.equals("ROLE_VISITOR")) {
            // VISITOR: memberId로 조회
            userDetails = customUserDetailService.loadMemberById(Long.parseLong(account));
        } else {
            // SUPER_ADMIN, AGENCY: loginId로 조회
            userDetails = customUserDetailService.loadUserByUsername(account);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
