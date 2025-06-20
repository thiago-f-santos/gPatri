package br.edu.ifg.numbers.gpatri.msusuarios.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Algorithm algoritimo = Algorithm.HMAC512(jwtSecret);

        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withClaim("permissoes", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + jwtExpirationMs))
                .sign(algoritimo);
    }

    public boolean validaToken(String token) {
        try {
            Algorithm algoritimo = Algorithm.HMAC512(jwtSecret);
            JWTVerifier verifier = JWT.require(algoritimo).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            logger.error("Token inválido ou expirado: ", e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao validar token: ", e.getMessage());
        }
        return false;
    }

    public String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            logger.error("Erro ao decodificar o token: ", e.getMessage());
            return null;
        }
    }

    public String getPermissoes(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("permissoes").asString();
        } catch (JWTVerificationException e) {
            logger.error("Erro ao decodificar o token: ", e.getMessage());
            return null;
        }
    }
}
