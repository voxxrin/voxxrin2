package voxxrin2.publisher;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.scribe.services.Base64Encoder;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.factory.Component;
import voxxrin2.auth.TwitterSettings;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class TwitterPublisher extends AbstractPublisher {

    private static final Logger logger = LoggerFactory.getLogger(TwitterPublisher.class);

    public static final int MILLSISECONDS_MULTIPLICAND = 1000;
    public static final String HMAC_SHA1 = "HmacSHA1";
    private final TwitterSettings settings;

    public TwitterPublisher(TwitterSettings settings) {
        this.settings = settings;
    }

    @Override
    public void publish(String msg) {

        String url = "https://api.twitter.com/1.1/statuses/update.json";
        String method = "POST";
        String message = "";

        String authorization = buildAuthorization(method, url, message);

        HttpRequest request = HttpRequest.post(url, true)
                .header("Authorization", authorization)
                .contentType("application/x-www-form-urlencoded")
                .send("status=" + OAuthEncoder.encode(message));

        logger.debug(request.toString());
        logger.debug("Authorization: {}", authorization);

        if (!request.ok()) {
            logger.error("Can't tweet message {}: ({}) {}", msg, request.code(), request.body());
        }
    }

    String buildAuthorization(String method,
                              String url,
                              String message,
                              Optional<String> timestampOptional,
                              Optional<String> uuidOptional,
                              Map<String, String> queryParams) {

        String timestamp = timestampOptional.or(Long.toString(System.currentTimeMillis() / MILLSISECONDS_MULTIPLICAND));
        String uuid = uuidOptional.or(UUID.randomUUID().toString());

        Map<String, String> paramsWithoutSignature =
                ImmutableMap.<String, String>builder()
                        .put("oauth_consumer_key", settings.oauthTwitterApiKey())
                        .put("oauth_nonce", uuid)
                        .put("oauth_signature_method", "HMAC-SHA1")
                        .put("oauth_timestamp", timestamp)
                        .put("oauth_token", settings.oauthTwitterAccessToken())
                        .put("oauth_version", "1.0")
                        .build();

        String signature = sign(method, url, ImmutableMap.<String, String>builder()
                .putAll(paramsWithoutSignature)
                .putAll(queryParams)
                .put("status", message)
                .build());

        return "OAuth " + percendEncode(ImmutableMap.<String, String>builder()
                .putAll(paramsWithoutSignature)
                .put("oauth_signature", signature)
                .build());
    }

    private String buildAuthorization(String method, String url, String message) {
        return buildAuthorization(
                method,
                url,
                message,
                Optional.<String>absent(),
                Optional.<String>absent(),
                Collections.<String, String>emptyMap()
        );
    }

    private String sign(String method, String url, Map<String, String> paramsToSign) {
        String signingKey = buildSigningKey();
        String signatureBaseString = buildSignatureBaseString(method, url, paramsToSign);

        SecretKeySpec key = new SecretKeySpec(signingKey.getBytes(), HMAC_SHA1);

        Mac mac;
        byte[] rawHmac;
        try {
            mac = Mac.getInstance(HMAC_SHA1);
            mac.init(key);
            rawHmac = mac.doFinal(signatureBaseString.getBytes());
            return Base64Encoder.getInstance().encode(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Unable to sign request", e);
        }
    }

    private String buildSignatureBaseString(String method, String url, Map<String, String> paramsToSign) {

        List<String> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : paramsToSign.entrySet()) {
            params.add(OAuthEncoder.encode(entry.getKey()) + "=" + OAuthEncoder.encode(entry.getValue()));
        }

        return method.toUpperCase() + "&" + OAuthEncoder.encode(url) + "&" + OAuthEncoder.encode(Joiner.on("&").join(params));
    }

    private String buildSigningKey() {
        return OAuthEncoder.encode(settings.oauthTwitterApiSecret()) + "&" + OAuthEncoder.encode(settings.oauthTwitterTokenSecret());
    }

    private String percendEncode(Map<String, String> parametersMap) {
        List<String> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
            params.add(OAuthEncoder.encode(entry.getKey()) + "=" + "'" + OAuthEncoder.encode(entry.getValue()) + "'");
        }
        return Joiner.on(", ").join(params);
    }
}
