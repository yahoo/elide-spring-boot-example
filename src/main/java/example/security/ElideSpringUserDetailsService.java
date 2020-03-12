package example.security;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class ElideSpringUserDetailsService implements UserDetailsService {

    private static final String TEST_USER = "test";
    private static final String TEST_PASSWORD = "{noop}password"; //noop for plain-text
    
    public ElideSpringUserDetailsService() {}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(TEST_USER.equals(username)) {
            return new ElideSpringUser(username, TEST_PASSWORD, new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with login: " + username);
        }
    }
}
