
package cl.peluditos.ws;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class CorsFilter implements ContainerRequestFilter,
        ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {

        if ("OPTIONS".equalsIgnoreCase(
                requestContext.getMethod())) {

            requestContext.abortWith(
                    Response.ok().build()
            );
        }
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext)
            throws IOException {

        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Origin", "*");

        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");

        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Headers",
                "Content-Type, Authorization");
    }
}
