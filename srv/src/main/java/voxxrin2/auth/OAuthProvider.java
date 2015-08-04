package voxxrin2.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.RestxRequest;
import restx.RestxResponse;
import restx.WebException;
import restx.http.HttpStatus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public abstract class OAuthProvider {

    private static final Logger logger = LoggerFactory.getLogger(OAuthProvider.class);

    private String provider;
    private String providerUrl;

    public OAuthProvider(String provider, String providerUrl) {
        this.provider = provider;
        this.providerUrl = providerUrl;
    }

    public abstract <T extends Map<String, ? extends List<String>>> Token authenticate(T params, RestxRequest request);

    public String getProvider() {
        return provider;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    protected static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Can't encodeUrl redirect_uri", e);
            throw new IllegalStateException("Can't encodeUrl redirect_uri", e);
        }
    }

    protected Token redirectTo(final String url) {
        throw new WebException(HttpStatus.FOUND) {
            @Override
            public void writeTo(RestxRequest restxRequest, RestxResponse restxResponse) throws IOException {
                restxResponse
                        .setStatus(getStatus())
                        .setHeader("Location", url);
            }
        };
    }
}