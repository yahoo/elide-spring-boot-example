package example.model;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.Paginate;
import com.yahoo.elide.annotation.PaginationMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Users.
 */
@Include(name = "users", description = "Users.", friendlyName = "User")
@Table(name = "AppUser")
@Entity
@Data
@Paginate(modes = { PaginationMode.OFFSET, PaginationMode.CURSOR })
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @SequenceGenerator(name = "USER_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "username")
    private String username = "";

    @Column(name = "name")
    private String name = "";
}
