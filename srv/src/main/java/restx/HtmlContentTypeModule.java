package restx;

import com.google.common.base.Optional;
import restx.entity.AbstractEntityResponseWriter;
import restx.entity.EntityDefaultContentTypeProvider;
import restx.entity.EntityResponseWriter;
import restx.entity.EntityResponseWriterFactory;
import restx.factory.Module;
import restx.factory.Provides;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@Module
public class HtmlContentTypeModule {

    private static final Collection HTML_TYPES = Arrays.asList(
            String.class, StringBuilder.class, StringBuffer.class
    );
    public static final String CONTENT_TYPE = "text/html";

    @Provides
    public EntityDefaultContentTypeProvider htmlEntityDefaultContentTypeProvider() {
        return new EntityDefaultContentTypeProvider() {
            @Override
            public Optional<String> mayProvideDefaultContentType(Type type) {
                if (HTML_TYPES.contains(type)) {
                    return Optional.of(CONTENT_TYPE);
                } else {
                    return Optional.absent();
                }
            }
        };
    }

    @Provides
    public EntityResponseWriterFactory htmlEntityResponseWriterFactory() {
        return new EntityResponseWriterFactory() {
            @Override
            public <T> Optional<? extends EntityResponseWriter<T>> mayBuildFor(Type valueType, String contentType) {
                if (!contentType.toLowerCase(Locale.ENGLISH).startsWith(CONTENT_TYPE)) {
                    return Optional.absent();
                }

                return Optional.of(new AbstractEntityResponseWriter<T>(valueType, CONTENT_TYPE) {
                    @Override
                    protected void write(T value, RestxRequest req, RestxResponse resp, RestxContext ctx) throws
                            IOException {
                        resp.getWriter().append(value.toString());
                    }
                });
            }
        };
    }

}
