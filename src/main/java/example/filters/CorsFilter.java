package example.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext request,
                       ContainerResponseContext response) throws IOException {
        String accessControlHeaders = request.getHeaderString("Access-Control-Request-Headers");
        String origin = request.getHeaderString("Origin");

        MultivaluedMap<String, Object> requestHeaders = response.getHeaders();
        requestHeaders.putSingle("Vary", "Origin");
        requestHeaders.putSingle("Access-Control-Allow-Methods", "OPTIONS,GET,POST,PATCH,DELETE");
        requestHeaders.putSingle("Access-Control-Allow-Headers",
                accessControlHeaders != null ? accessControlHeaders : "");
        requestHeaders.putSingle("Access-Control-Allow-Origin", origin != null ? origin : "*");
        requestHeaders.putSingle("Access-Control-Allow-Credentials", "true");
    }
}
