package example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.yahoo.elide.annotation.EntityId;
import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.Paginate;
import com.yahoo.elide.annotation.PaginationMode;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Discussion message posts.
 * <p>
 * This is used to demonstrate the use of {@link EntityId}.
 */
@Include(name = "posts", description = "Discussion message posts.", friendlyName = "Post")
@Table(name = "post")
@Entity
@Data
@Paginate(modes = { PaginationMode.OFFSET, PaginationMode.CURSOR })
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_SEQ")
    @SequenceGenerator(name = "POST_SEQ", allocationSize = 1)
    private Long id;

    @EntityId
    @NotNull
    @Column(name = "entity_id")
    private String entityId;

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
    private Post parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> replies = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (this.entityId == null || this.entityId.length() != 36) {
            this.entityId = UUID.randomUUID().toString();
        }
    }
}
