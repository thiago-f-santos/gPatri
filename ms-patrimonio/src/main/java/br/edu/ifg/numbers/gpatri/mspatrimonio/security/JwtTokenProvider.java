package br.edu.ifg.numbers.gpatri.mspatrimonio.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    public boolean validaToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC512(jwtSecret);
            JWTVerifier verifier = JWT.require(algoritmo).build();
            verifier.verify(token); // Tenta verificar a assinatura e a expiração do token
            return true;
        } catch (JWTVerificationException e) {
            log.error("Token inválido ou expirado: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage());
        }
        return false;
    }

    public String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            log.error("Erro ao decodificar o token para obter o username: {}", e.getMessage());
            return null;
        }
    }

    public String getPermissoes(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("permissoes").asString();
        } catch (JWTVerificationException e) {
            log.error("Erro ao decodificar o token para obter as permissões: {}", e.getMessage());
            return null;
        }
    }

    public UUID getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String userIdString = decodedJWT.getClaim("userId").asString();
            if (userIdString != null) {
                return UUID.fromString(userIdString);
            }
            return null;
        } catch (JWTVerificationException e) {
            log.error("Erro ao decodificar o token para obter o userId: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Claim 'userId' no token não é um UUID válido: {}", e.getMessage());
            return null;
        }
    }
}
