/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example.models;

import com.yahoo.elide.annotation.Include;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;

@Include(rootLevel = false, name = "version")
@Table(name = "artifactversion")
@Entity
public class ArtifactVersion {
    @Id
    private String name = "";

    private Date createdAt = new Date();

    @ManyToOne
    private ArtifactProduct artifact;
}
