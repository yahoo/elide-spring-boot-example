/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example.models.v1;

import com.yahoo.elide.annotation.Include;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Include(rootLevel = false, name = "productV1", description = "Artifact product.", friendlyName = "ProductV1")
@Table(name = "artifactproduct")
@Entity
public class ArtifactProductV1 {
    @Id
    private String name = "";

    private String commonName = "";

    private String description = "";

    @ManyToOne
    private ArtifactGroupV1 group = null;

    @OneToMany(mappedBy = "artifact")
    private List<ArtifactVersionV1> versions = new ArrayList<>();
}
