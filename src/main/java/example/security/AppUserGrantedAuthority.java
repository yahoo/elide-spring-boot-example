package example.security;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * {@link GrantedAuthority} to store the user's id.
 */
@Data
@RequiredArgsConstructor
public class AppUserGrantedAuthority implements GrantedAuthority {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Long id;

    @Override
    public String getAuthority() {
        return this.id.toString();
    }
}
