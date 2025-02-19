/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example.model;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.annotation.Paginate;
import com.yahoo.elide.annotation.PaginationMode;
import com.yahoo.elide.graphql.subscriptions.annotations.Subscription;
import com.yahoo.elide.graphql.subscriptions.annotations.SubscriptionField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Include(name = "groups", description = "Artifact group.", friendlyName = "Group")
@Table(name = "artifactgroup")
@Entity
@Subscription
@Data
@Paginate(modes = { PaginationMode.CURSOR, PaginationMode.OFFSET })
public class ArtifactGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_SEQ")
    @SequenceGenerator(name = "GROUP_SEQ", allocationSize = 1)
    private Long id;

    private String name = "";

    @SubscriptionField
    @NotNull
    private String commonName = "";

    @SubscriptionField
    private String description = "";

    @NotNull
    private OffsetDateTime createdOn = OffsetDateTime.now();

    @NotNull
    private String createdBy = "user";

    @SubscriptionField
    @OneToMany(mappedBy = "group")
    @EqualsAndHashCode.Exclude
    private List<ArtifactProduct> products = new ArrayList<>();
}
