package example.repository;

import org.springframework.data.repository.Repository;

import example.model.ArtifactGroup;

/**
 * {@link Repository} for {@link ArtifactGroup}.
 */
public interface ArtifactGroupRepository extends QueryRepository<ArtifactGroup, Long> {
}
