package rs.raf.ProbaVebProjekat.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    public String generateToken(Claims claims){
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256,jwtSecret).compact();

    }
    public Claims parseToken(String jwt){
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
        }catch (Exception e){
            return null;
        }
        return claims;
    }
}
