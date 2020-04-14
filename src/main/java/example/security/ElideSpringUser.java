package example.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class ElideSpringUser extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;
    private String name;

    public ElideSpringUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password,  authorities);
        this.name = username;
    }

    public String getName() {
        return name;
    }
}