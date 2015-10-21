package crawlers.web;

import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import restx.config.ConfigLoader;
import restx.config.ConfigSupplier;
import restx.factory.Module;
import restx.factory.Provides;
import restx.security.CORSAuthorizer;
import restx.security.SignatureKey;
import restx.security.StdCORSAuthorizer;

@Module
public class AppModule {

    @Provides
    public SignatureKey signatureKey() {
        return new SignatureKey("crawlers a9963b3f-e823-4f37-a494-507f8759a24f crawlers -8865615259346873016".getBytes(Charsets.UTF_8));
    }

    @Provides
    public CORSAuthorizer CORSAuthorizer() {
        StdCORSAuthorizer.Builder builder = StdCORSAuthorizer.builder();
        return builder.setOriginMatcher(Predicates.<CharSequence>alwaysTrue())
                .setAllowedHeaders(ImmutableList.of("Origin", "X-Requested-With", "Content-Type", "Accept", "If-Modified-Since"))
                .setPathMatcher(Predicates.<CharSequence>alwaysTrue())
                .setAllowedMethods(ImmutableList.of("GET", "POST", "PUT", "OPTIONS", "DELETE"))
                .build();
    }
}
