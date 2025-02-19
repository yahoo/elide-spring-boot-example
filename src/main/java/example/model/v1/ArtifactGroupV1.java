/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example.model.v1;

import com.yahoo.elide.annotation.Include;
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
import java.util.ArrayList;
import java.util.List;

@Include(name = "group", description = "Artifact group.", friendlyName = "GroupV1")
@Table(name = "artifactgroup")
@Entity
@Subscription
@Data
public class ArtifactGroupV1 {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_SEQ")
    @SequenceGenerator(name = "GROUP_SEQ", allocationSize = 1)
    private Long id;

    private String name = "";

    @SubscriptionField
    private String commonName = "";

    @SubscriptionField
    private String description = "";

    @SubscriptionField
    @OneToMany(mappedBy = "group")
    @EqualsAndHashCode.Exclude
    private List<ArtifactProductV1> products = new ArrayList<>();
}
