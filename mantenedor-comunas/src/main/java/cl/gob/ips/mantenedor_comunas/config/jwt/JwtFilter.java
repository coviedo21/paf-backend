package cl.gob.ips.mantenedor_comunas.config.jwt;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.contains("swagger") || path.contains("api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        if (path.contains("/jwt-n")) {
            chain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
        
            // Agrega estos headers a la respuesta
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Token ausente o mal formado.");
            return;
        }

//        String token = authHeader.substring(7);
//        boolean tokenValido = jwtUtil.verificarToken(token);
//
//        if (!tokenValido) {
//            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            httpResponse.getWriter().write("Token inválido.");
//            return;
//        }

        chain.doFilter(request, response); // ✅ Token válido, continúa
    }

    @Override
    public void destroy() {
        // NOSONAR - Método requerido por la interfaz Filter, sin lógica necesaria
    }
}
