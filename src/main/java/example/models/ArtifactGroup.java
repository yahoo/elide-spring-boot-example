/*
 * Copyright 2019, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package example.models;

import com.yahoo.elide.annotation.Include;
import com.yahoo.elide.graphql.subscriptions.annotations.Subscription;
import com.yahoo.elide.graphql.subscriptions.annotations.SubscriptionField;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Include(name = "group")
@Table(name = "artifactgroup")
@Entity
@Subscription
@Data
public class ArtifactGroup {
    @Id
    private String name = "";

    @SubscriptionField
    private String commonName = "";

    @SubscriptionField
    private String description = "";

    @SubscriptionField
    @OneToMany(mappedBy = "group")
    private List<ArtifactProduct> products = new ArrayList<>();
}
