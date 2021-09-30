package ar.com.redsocial.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	
	private String jwtSecret = "JWTSuperSecretKey";
	
	private int jwtExpirationInMs = 604800000;
	
    public String generateToken(Authentication authentication) {

        UsersPrincipal userPrincipal = (UsersPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }	
	
	
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }	

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Firma JWT no válida");
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT no válido");
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT caducado");
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no admitido");
        } catch (IllegalArgumentException ex) {
            logger.error("La cadena de reclamaciones de JWT está vacía.");
        }
        return false;
    }    
    
}
