package ddt.spring.security.auth0;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class Auth0AuthenticationFilter extends GenericFilterBean {
	
	private AuthenticationManager authenticationManager;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		System.out.println("CONTEXT" + SecurityContextHolder.getContext() + " CLASS AUTH MANAGER" + authenticationManager.getClass().getCanonicalName());

		String jwt = getToken((HttpServletRequest) request);
		
		System.out.println("Found token" + jwt);

		if (jwt != null) {
		
			Auth0JWTToken token = new Auth0JWTToken(jwt);
			Authentication authResult = authenticationManager.authenticate(token);
			
			SecurityContextHolder.getContext().setAuthentication(authResult);
			
			chain.doFilter(request, response);
		
		
		} else {
			throw new AccessDeniedException("You must provide an apikey");
		}

		
		
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	
	
	 private String getToken(HttpServletRequest httpRequest) throws ServletException {
	    	String token = null;
	        final String authorizationHeader = httpRequest.getHeader("authorization");
	        if (authorizationHeader == null) {
	            throw new ServletException("Unauthorized: No Authorization header was found");
	        }

	        String[] parts = authorizationHeader.split(" ");
	        if (parts.length != 2) {
	            throw new ServletException("Unauthorized: Format is Authorization: Bearer [token]");
	        }

	        String scheme = parts[0];
	        String credentials = parts[1];

	        Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
	        if (pattern.matcher(scheme).matches()) {
	            token = credentials;
	        }
	        return token;
	    }
	
}