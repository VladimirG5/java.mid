package hr.abysalto.hiring.mid.security;

import hr.abysalto.hiring.mid.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(10)
public class UserMDCFilter extends OncePerRequestFilter {

    private static final String USER_ID_KEY = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String userId = getAuthenticatedUserId();

            if (userId != null) {
                MDC.put(USER_ID_KEY, userId);
            }

            filterChain.doFilter(request, response);

        } finally {
            // Always clear MDC after request to prevent memory leaks
            MDC.clear();
        }
    }

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return ((User) principal).getId().toString();
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }
}