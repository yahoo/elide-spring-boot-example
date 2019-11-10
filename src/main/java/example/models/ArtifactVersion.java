package example.models;

import com.yahoo.elide.annotation.Include;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Include(type = "version")
@Entity
public class ArtifactVersion {
    @Id
    private String name = "";

    private Date createdAt = new Date();

    @ManyToOne
    private ArtifactProduct artifact;
}
