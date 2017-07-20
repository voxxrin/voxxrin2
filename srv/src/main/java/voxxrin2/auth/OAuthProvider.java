package voxxrin2.auth;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restx.RestxRequest;
import restx.RestxResponse;
import restx.WebException;
import restx.http.HttpStatus;
import voxxrin2.domain.User;

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

    public abstract <T extends Map<String, ?>> Optional<User> authenticate(Optional<T> params) throws IOException;

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

    protected User redirectTo(final String url) {
        throw new WebException(HttpStatus.FOUND) {
            @Override
            public void writeTo(RestxRequest restxRequest, RestxResponse restxResponse) throws IOException {
                restxResponse
                        .setStatus(getStatus())
                        .setHeader("Location", url)
                        .setHeader("Access-Control-Allow-Headers", "*")
                        .setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS")
                        .setHeader("Access-Control-Allow-Origin", "*");
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <T extends Map<String, ?>> Map<String, List<String>> castParams(Optional<T> params) {
        return (Map<String, List<String>>) params.get();
    }

    protected Optional<String> extractFirstParam(List<String> list) {
        Optional<String> param;
        if (list != null) {
            param = Optional.fromNullable(Iterables.getFirst(list, null));
        } else {
            param = Optional.absent();
        }
        return param;
    }
}