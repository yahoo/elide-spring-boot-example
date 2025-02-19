/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example.model;

import com.yahoo.elide.annotation.Include;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Include(rootLevel = false, name = "products", description = "Artifact product.", friendlyName = "Product")
@Table(name = "artifactproduct")
@Entity
@Data
public class ArtifactProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT_SEQ")
    @SequenceGenerator(name = "PRODUCT_SEQ", allocationSize = 1)
    private Long id;

    private String name = "";

    private String commonName = "";

    private String description = "";

    @ManyToOne
    @EqualsAndHashCode.Exclude
    private ArtifactGroup group = null;

    @OneToMany(mappedBy = "product")
    @EqualsAndHashCode.Exclude
    private List<ArtifactVersion> versions = new ArrayList<>();
}
