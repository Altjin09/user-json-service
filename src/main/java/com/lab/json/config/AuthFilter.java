package com.lab.json.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lab.json.client.SoapAuthClient;
import com.lab.json.client.ValidateResult;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private SoapAuthClient soapAuthClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
    	
    	if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
    	    response.setStatus(HttpServletResponse.SC_OK);
    	    filterChain.doFilter(request, response);
    	    return;
    	}

        String path = request.getRequestURI();

        if (path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("Missing Authorization");
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("Extracted token: " + token);

        ValidateResult result = soapAuthClient.validateToken(token);
        System.out.println("SOAP validate result valid = " + result.isValid());
        System.out.println("SOAP validate result userId = " + result.getUserId());
        System.out.println("SOAP validate result username = " + result.getUsername());

        if (!result.isValid()) {
            response.setStatus(401);
            response.getWriter().write("Invalid token");
            return;
        }

        request.setAttribute("userId", result.getUserId());
        request.setAttribute("username", result.getUsername());

        filterChain.doFilter(request, response);
    }
}