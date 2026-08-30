package com.pulsebysolutions.onlinebookstoreinternshipproject.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // replace the header reading with:
        final String jwt = extractToken(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try{
            final String employeeCode = jwtService.extractSubject(jwt);

            // If user is not yet "logged in" to this request
            if (employeeCode != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(employeeCode);

                if (jwtService.isTokenValid(jwt)) {
                    // This object tells Spring: "This user is valid and has these roles"
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // The most important line: putting the user in the "Security Box"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.warn("Expired JWT token, clearing cookie");
            SecurityContextHolder.clearContext();

            Cookie expiredCookie = new Cookie("user_token", "");
            expiredCookie.setMaxAge(0);
            expiredCookie.setPath("/");
            expiredCookie.setHttpOnly(true);
            expiredCookie.setSecure(true);
            expiredCookie.setAttribute("SameSite", "None");
            response.addCookie(expiredCookie);
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // Malformed, tampered with, or pointing at a user who no longer
            // exists. A bad token is not a server fault: leave the request
            // unauthenticated so the entry point answers 401 (spec §7.7)
            // instead of letting this escape the chain as a 500.
            logger.warn("Rejected invalid JWT: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }


        filterChain.doFilter(request, response);
    }
    private String extractToken(HttpServletRequest request) {
        // try header first
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // fall back to cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
