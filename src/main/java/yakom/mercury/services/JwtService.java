package yakom.mercury.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import yakom.mercury.model.RequestType;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static java.util.Collections.singletonMap;

public class JwtService {

    private final Key key;
    private final Long validityMinutes;

    public JwtService(@Nullable String secret,
                      @Nullable Long validityMinutes) {

        key = (null == secret) ? secretKeyFor(HS512)
                : hmacShaKeyFor(secret.getBytes());
        this.validityMinutes = (null == validityMinutes)
                ? 60L : validityMinutes;
    }

    public String generate(
            @NonNull String username,
            @NonNull RequestType requestType) {
        return Jwts
                .builder()
                .setSubject(username)
                .setExpiration(getExpirationDate())
                .addClaims(singletonMap("type", requestType.name()))
                .signWith(key)
                .compact();
    }

    private Date getExpirationDate() {
        return Date.from(
                ZonedDateTime.now()
                        .plusMinutes(validityMinutes)
                        .toInstant());
    }

    public String validate(
            @NonNull String jwt,
            @NonNull RequestType requestType) {

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        String requestTypeClaim = claims
                .get("type", String.class);
        if (!requestTypeClaim.equals(requestType.name()))
            throw new BadCredentialsException(
                    "Request type invalid or missing.");

        return claims.getSubject();
    }
}
