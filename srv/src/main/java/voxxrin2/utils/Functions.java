package voxxrin2.utils;

import com.google.common.base.Function;
import voxxrin2.auth.OAuthProvider;

public class Functions {

    public static Function<OAuthProvider, String> OAUTH_PROVIDER_MAP_INDEXER = new Function<OAuthProvider, String>() {
        @Override
        public String apply(OAuthProvider input) {
            return input.getProvider();
        }
    };
}
