package com.voluntiex.voluntiexBackend.utils;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtTokenUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(key)
                .compact();
    }

    public static String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private static Claims getClaimsFromToken(String token) {
        return ((JwtParser) Jwts.parser()
                .setSigningKey(key))
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static Date extractExpiration(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public static boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
