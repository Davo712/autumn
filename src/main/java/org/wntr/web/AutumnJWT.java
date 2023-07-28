package org.wntr.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Map;

public class AutumnJWT {


    static String SECRET_KEY = "first";


    private static Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
    private static JWTVerifier verifier = JWT.require(algorithm)
            .build();

    public static String createJWT(Map map) {
        return JWT.create()
                .withKeyId(SECRET_KEY)
                .withClaim("parameters", map)
                .sign(algorithm);
    }

    public static boolean checkJWT(String jwtToken) {
        try {
            DecodedJWT decodedJWT = verifier.verify(jwtToken);
            Claim claim = decodedJWT.getClaim("parameters");
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
