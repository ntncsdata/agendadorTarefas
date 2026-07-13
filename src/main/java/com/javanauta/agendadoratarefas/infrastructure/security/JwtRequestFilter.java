package com.javanauta.agendadoratarefas.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtRequestFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;

        public JwtRequestFilter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain) throws ServletException, IOException {

            final String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                final String token = header.substring(7);
                try {
                    final String email = jwtUtil.extrairEmailToken(token);

                    if (email != null
                            && SecurityContextHolder.getContext().getAuthentication() == null
                            && jwtUtil.validateToken(token, email)) {

                        String role = jwtUtil.extrairRoleToken(token);

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        email, null, List.of(new SimpleGrantedAuthority(role)));

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                }
            }
            chain.doFilter(request, response);
        }
}

