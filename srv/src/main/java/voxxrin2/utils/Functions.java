package voxxrin2.utils;

import com.google.common.base.Function;
import voxxrin2.auth.OAuthProvider;

public class Functions {

    public static final Function<OAuthProvider, String> PROVIDERS_LIST_TO_MAP_FN = new Function<OAuthProvider, String>() {
        @Override
        public String apply(OAuthProvider input) {
            return input.getProvider();
        }
    };
}
