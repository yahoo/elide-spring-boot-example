package example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.yahoo.elide.annotation.DeletePermission;
import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.LifeCycleHookBinding;
import com.yahoo.elide.annotation.LifeCycleHookBinding.Operation;
import com.yahoo.elide.annotation.LifeCycleHookBinding.TransactionPhase;
import com.yahoo.elide.annotation.Paginate;
import com.yahoo.elide.annotation.PaginationMode;
import com.yahoo.elide.annotation.ReadPermission;
import com.yahoo.elide.annotation.UpdatePermission;
import com.yahoo.elide.core.lifecycle.LifeCycleHook;
import com.yahoo.elide.core.security.ChangeSpec;
import com.yahoo.elide.core.security.RequestScope;
import com.yahoo.elide.core.security.User;

import example.security.AppUserGrantedAuthority;
import example.security.check.NoteCreatorIsUserCheck;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Notes.
 * <p>
 * This is used to demonstrate the use of id obfuscation.
 */
@Include(name = "notes", description = "Notes.", friendlyName = "Note")
@Table(name = "note")
@Entity
@Data
@Paginate(modes = { PaginationMode.OFFSET, PaginationMode.CURSOR })
@ReadPermission(expression = NoteCreatorIsUserCheck.NOTE_CREATOR_IS_USER)
@UpdatePermission(expression = NoteCreatorIsUserCheck.NOTE_CREATOR_IS_USER)
@DeletePermission(expression = NoteCreatorIsUserCheck.NOTE_CREATOR_IS_USER)
@LifeCycleHookBinding(operation = LifeCycleHookBinding.Operation.CREATE, phase = LifeCycleHookBinding.TransactionPhase.PRESECURITY, hook = example.model.Note.NoteHook.class)
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTE_SEQ")
    @SequenceGenerator(name = "NOTE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "title")
    private String title = "";

    @Column(name = "content")
    private String content = "";

    @Column(name = "content_html")
    private String contentHtml = "";

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="created_by")
    private AppUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="parent_id")
    private Note parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> replies = new ArrayList<>();

    /**
     * {@link LifeCycleHook} that sets created by.
     */
    public static class NoteHook implements LifeCycleHook<Note> {
        @Override
        public void execute(Operation operation, TransactionPhase phase, Note elideEntity, RequestScope requestScope,
                Optional<ChangeSpec> changes) {
            User user = requestScope.getUser();
            if (user != null) {
                if (user.getPrincipal() instanceof Authentication authentication && authentication.isAuthenticated()) {
                    for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                        if (grantedAuthority instanceof AppUserGrantedAuthority appUserGrantedAuthority) {
                            AppUser appUser = new AppUser();
                            appUser.setId(appUserGrantedAuthority.getId());
                            appUser.setUsername(user.getName());
                            elideEntity.setCreatedBy(appUser);
                        }
                    }
                }
            }
        }
    }
}
