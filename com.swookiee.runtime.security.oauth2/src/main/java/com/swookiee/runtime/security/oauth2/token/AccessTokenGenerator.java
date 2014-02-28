package com.swookiee.runtime.security.oauth2.token;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import net.oauth.jsontoken.Clock;
import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.SystemClock;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

import org.joda.time.Instant;
import org.joda.time.Period;

/**
 * {@link AccessTokenGenerator} generates a OAuth2 access token.
 * 
 */
public class AccessTokenGenerator {

    // TODO: use configured hours of validity
    public static final int HOURS_OF_TOKEN_VALIDITY = 8;

    private static final String AUDIENCE = "swookiee";

    // TODO: use configured shared secret
    private static final String SHARED_SECRET = "test";

    private final Clock clock = new SystemClock();

    public String generate(String userId) throws TokenCreationException {

        try {
            HmacSHA256Signer signer = new HmacSHA256Signer(userId, null, SHARED_SECRET
                    .getBytes());

            JsonToken token = new JsonToken(signer);

            token.setAudience(AUDIENCE);

            Instant now = now();
            Instant expiration = now().plus(Period.hours(HOURS_OF_TOKEN_VALIDITY).toStandardDuration());

            token.setIssuedAt(now);
            token.setExpiration(expiration);

            String serializedToken = token.serializeAndSign();

            return serializedToken;
        } catch (InvalidKeyException | SignatureException ex) {
            throw new TokenCreationException(String.format("Could not generate token: %s", ex.getMessage()), ex);
        }
    }

    private Instant now() {
        return clock.now();
    }
}
