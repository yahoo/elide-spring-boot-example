package example.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * {@link Repository} for querying.
 * <p>
 * This is for demonstration purposes only.
 */
@NoRepositoryBean
public interface QueryRepository<T, ID> extends Repository<T, ID>, JpaSpecificationExecutor<T> {
}
