package voxxrin2.filters;

import com.google.common.base.Optional;
import restx.*;
import restx.factory.Component;
import voxxrin2.domain.technical.Reference;
import voxxrin2.persistence.ReferenceResolver;

import java.io.IOException;

@Component(priority = -250)
public class ReferenceResolverFilter implements RestxFilter, RestxHandler {

    private final ReferenceResolver referenceResolver;

    public ReferenceResolverFilter(ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
    }

    @Override
    public Optional<RestxHandlerMatch> match(RestxRequest restxRequest) {
        return Optional.of(new RestxHandlerMatch(new StdRestxRequestMatch(restxRequest.getRestxPath()), this));
    }

    @Override
    public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws IOException {
        try {
            Reference.getResolver().set(referenceResolver);
            ctx.nextHandlerMatch().handle(req, resp, ctx);
        } finally {
            Reference.getResolver().remove();
        }
    }
}
