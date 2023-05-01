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
import jakarta.persistence.Table;
import java.util.Date;

@Include(rootLevel = false, name = "versionV1", description = "Artifact version.", friendlyName = "VersionV1")
@Table(name = "artifactversion")
@Entity
public class ArtifactVersionV1 {
    @Id
    private String name = "";

    private Date createdAt = new Date();

    @ManyToOne
    private ArtifactProductV1 artifact;
}
