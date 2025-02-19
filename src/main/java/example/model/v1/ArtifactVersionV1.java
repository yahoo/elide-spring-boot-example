/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example.model.v1;

import com.yahoo.elide.annotation.Include;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Include(rootLevel = false, name = "version", description = "Artifact version.", friendlyName = "VersionV1")
@Table(name = "artifactversion")
@Entity
@Data
public class ArtifactVersionV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VERSION_SEQ")
    @SequenceGenerator(name = "VERSION_SEQ", allocationSize = 1)
    private Long id;

    private String name = "";

    @NotNull
    private OffsetDateTime createdOn = OffsetDateTime.now();

    @ManyToOne
    @EqualsAndHashCode.Exclude
    private ArtifactProductV1 product;
}
