package example.service;

import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for querying.
 * <p>
 * This is for demonstration purposes only.
 *
 * @see example.repository.QueryRepository
 */
@Slf4j
public class QueryService {
    private final Map<Class<?>, JpaSpecificationExecutor<?>> repositories;

    public QueryService(Map<Class<?>, JpaSpecificationExecutor<?>> repositories) {
        this.repositories = repositories;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Window<?> query(Class<?> entity, Specification specification, Sort sort, Limit limit,
            ScrollPosition position) {
        log.info("Querying for [{}] sort {} limit {} position {}", entity.getSimpleName(), sort, limit, position);
        return (Window<?>) repositories.get(entity)
                .findBy(specification, query -> query.sortBy(sort).limit(limit.max()).scroll(position));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public long count(Class<?> entity, Specification specification) {
        return repositories.get(entity).count(specification);
    }

    public Set<Class<?>> getEntities() {
        return repositories.keySet();
    }
}
