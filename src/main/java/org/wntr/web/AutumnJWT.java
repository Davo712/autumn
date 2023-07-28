package org.wntr.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class AutumnJWT {


    static String SECRET_KEY = "first";
    static int timeoutHours = 3600;


    private static Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
    private static JWTVerifier verifier = JWT.require(algorithm)
            .build();

    public static String createJWT(Map map) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.add(Calendar.HOUR, timeoutHours);
        return JWT.create()
                .withExpiresAt(calendar.getTime().toInstant())
                .withKeyId(SECRET_KEY)
                .withClaim("parameters", map)
                .sign(algorithm);
    }

    public static boolean checkJWT(String jwtToken) {
        try {
            DecodedJWT decodedJWT = verifier.verify(jwtToken);
            if (decodedJWT.getExpiresAtAsInstant().isBefore(Instant.now())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getParam(String paramName, String jwtToken) {
        return verifier
                .verify(jwtToken)
                .getClaim("parameters")
                .asMap()
                .get(paramName)
                .toString();

    }

    public static Map getParamMap(String jwtToken) {
        return verifier
                .verify(jwtToken)
                .getClaim("parameters")
                .asMap();
    }
}
