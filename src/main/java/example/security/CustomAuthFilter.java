package example.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class CustomAuthFilter extends GenericFilterBean {
	
	@Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    	
    	ElideSpringUser user = new ElideSpringUser("test", "{noop}test", new ArrayList<>());
    	UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, user.getPassword(), new ArrayList<>());

    	SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authRequest);
        
        chain.doFilter(request, response);
    }
}
