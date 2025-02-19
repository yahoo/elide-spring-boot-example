package example.security.check;

import com.yahoo.elide.annotation.SecurityCheck;
import com.yahoo.elide.core.security.User;
import com.yahoo.elide.core.security.checks.UserCheck;

import example.config.AppSecurityProperties;

/**
 * {@link UserCheck} that the user has ROLE_ADMIN.
 */
@SecurityCheck(UserIsAdminCheck.USER_IS_ADMIN)
public class UserIsAdminCheck extends UserCheck {
    public static final String USER_IS_ADMIN = "User is Admin";

    private final AppSecurityProperties appSecurityProperties;

    public UserIsAdminCheck(AppSecurityProperties appSecurityProperties) {
        this.appSecurityProperties = appSecurityProperties;
    }

    @Override
    public boolean ok(User user) {
        if (this.appSecurityProperties.isEnabled()) {
            return user.isInRole("ROLE_ADMIN");
        } else {
            return true;
        }
    }
}
