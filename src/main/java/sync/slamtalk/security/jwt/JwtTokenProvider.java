package sync.slamtalk.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.security.dto.JwtTokenResponseDto;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 토큰 생성 및 유효성 검증 하는 클래스
 */
@Slf4j
@Component
@Transactional(readOnly = true)
public class JwtTokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String GRANT_TYPE = "Bearer ";
    private final String secretKey;
    /* AccessToken 설정 */
    private final Long accessTokenExpirationPeriod;
    /* RefreshToken 설정 */
    private final Long refreshTokenExpirationPeriod;
    private final UserRepository userRepository;
    private SecretKey key;

    public JwtTokenProvider(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.access.expiration}") long accessTokenExpirationPeriod,
            @Value("${jwt.refresh.expiration}") long refreshTokenExpirationPeriod,
            UserRepository userRepository) {
        this.secretKey = secretKey;
        this.accessTokenExpirationPeriod = accessTokenExpirationPeriod * 1000;
        this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriod * 1000;
        this.userRepository = userRepository;
    }

    /**
     * 디코딩된 바이트 배열을 HMAC SHA 알고리즘을 사용하는 키로 변환하는 메서드
     */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // 문자열 형태의 Base64 디코딩하여 바이트 배열로 변환
        this.key = Keys.hmacShaKeyFor(keyBytes); // HMAC SHA 알고리즘을 사용하는 키로 생성
    }

    /**
     * Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
     *
     * @param user
     * @return String
     */
    @Transactional
    public JwtTokenResponseDto createToken(User user) {

        // 권한 정보 가져오기
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.debug("authorities = {}", authorities);

        String accessToken = createAccessToken(user, authorities);
        String refreshToken = createRefreshToken(user, authorities);

        // refreshToken db 에 저장
/*        User userVO = userRepository.findById(user.getId())
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.LOGIN_FAIL));*/

        user.updateRefreshToken(refreshToken);

        return new JwtTokenResponseDto(GRANT_TYPE, accessToken, refreshToken);
    }

    /**
     * User 정보와 권환정보를 이용해서 AccessToken 발급 하는 메서드
     *
     * @param user        해당하는 유저
     * @param authorities 권한정보(UserRole)
     * @return accessToken
     */
    public String createAccessToken(User user, String authorities) {
        long now = (new Date()).getTime();
        Date accessTokenValidity = new Date(now + this.accessTokenExpirationPeriod);

        return Jwts.builder()
                .subject(String.valueOf(user.getId())) // 사용자이름 이름을 클레임으로 저장.
                .claim(AUTHORITIES_KEY, authorities) // 권한 정보를 저장
                .expiration(accessTokenValidity) // 토큰 만료 시간 저장
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * refreshToken 발급 하는 메서드
     *
     * @return refreshToken
     */
    public String createRefreshToken(User user, String authorities) {
        long now = (new Date()).getTime();
        Date refreshTokenValidity = new Date(now + this.refreshTokenExpirationPeriod);

        return Jwts.builder()
                .expiration(refreshTokenValidity) // 토큰 만료 시간 저장
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * accessToken을 복호화 해서 유저정보를 얻어오는 메서드
     *
     * @param accessToken
     * @return Authentication
     */
    public Authentication getAuthentication(String accessToken) {

        // Jwt 토큰 복호화
        Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(key) // 서명 검증
                .build()
                .parseSignedClaims(accessToken);

        Claims claims = claimsJws.getPayload();

        if (claims.get(AUTHORITIES_KEY) == null) {
            log.info("권한 정보가 없는 토큰입니다");
            throw new BaseException(UserErrorResponseCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.INVALID_TOKEN));

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        return new UsernamePasswordAuthenticationToken(user, accessToken, authorities);
    }

    /**
     * 토큰 검증하는 메서드
     *
     * @return true 검증 성공 / false 검증 실패
     */
    public boolean validateToken(String token) {
        try {
            // Jwt 토큰 복호화
            Jwts
                    .parser()
                    .verifyWith(key) // 서명 검증
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


    /**
     * RefreshToken으로 AccessToken 재발급하는 메서드
     * @param refreshToken
     * @return Optional<JwtTokenResponseDto>
     * */
    @Transactional
    public Optional<JwtTokenResponseDto> generateNewAccessToken(String refreshToken){
        log.debug("엑세스 토큰 재발급");
        //todo : db 조회하는거
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElse(null);

        if(user != null) {
            return Optional.of(createToken(user));
        }
        return Optional.empty();
    }
}