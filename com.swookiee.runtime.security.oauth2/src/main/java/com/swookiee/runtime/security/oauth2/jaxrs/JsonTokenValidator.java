package com.swookiee.runtime.security.oauth2.jaxrs;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.SystemClock;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;

@Component(service = {JsonTokenValidator.class}, configurationPid = "com.swookiee.runtime.security.oauth2")
public class JsonTokenValidator {

    private String sharedSecret;

    public String validate(String token) throws JsonTokenValidationException {

        JsonToken jsonToken = null;

        try {
            JsonTokenParser parser = createJsonTokenParser();
            jsonToken = parser.verifyAndDeserialize(token);
        } catch (InvalidKeyException ex) {
            throw new JsonTokenValidationException("Could not create Json Token Parser: " + ex.getMessage(), ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new JsonTokenValidationException("Could not create Json Token Parser: " + ex.getMessage(), ex);
        } catch (SignatureException ex) {
            throw new JsonTokenValidationException("Invalid token signature: " + ex.getMessage(), ex);
        } catch (JsonParseException ex) {
            throw new JsonTokenValidationException("Token could not be parsed: " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new JsonTokenValidationException("Token could not be parsed: " + ex.getMessage(), ex);
        } catch (IllegalStateException ex) {
            if (ex.getMessage().contains("Invalid iat and/or exp")) {
                throw new JsonTokenValidationException("Token expired: " + ex.getMessage(), ex);
            } else {
                throw new JsonTokenValidationException("Token could not be parsed: " + ex.getMessage(), ex);
            }
        } catch (Exception ex) {
            throw new JsonTokenValidationException("Unknown error: " + ex.getMessage(), ex);
        }

        return jsonToken.getIssuer();
    }

    private JsonTokenParser createJsonTokenParser() throws NoSuchAlgorithmException, InvalidKeyException {

        final Verifier hmacVerifier = new HmacSHA256Verifier(sharedSecret.getBytes());

        VerifierProvider hmacVerifierProvider = new VerifierProvider() {
            @Override
            public List<Verifier> findVerifier(String signerId, String keyId) {
                return Lists.newArrayList(hmacVerifier);
            }
        };

        VerifierProviders providers = new VerifierProviders();
        providers.setVerifierProvider(SignatureAlgorithm.HS256, hmacVerifierProvider);

        JsonTokenParser jsonTokenParser = new JsonTokenParser(new SystemClock(), providers);

        return jsonTokenParser;
    }

    @Modified
    private void modified(final Map<String, String> properties) {
        if (properties == null) {
            return;
        }

        this.sharedSecret = properties.get("sharedSecret");
    }

}
