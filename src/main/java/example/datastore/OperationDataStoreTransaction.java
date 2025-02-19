package example.datastore;

import java.io.IOException;
import java.util.Set;

import com.yahoo.elide.core.RequestScope;
import com.yahoo.elide.core.datastore.DataStoreIterable;
import com.yahoo.elide.core.datastore.DataStoreTransaction;
import com.yahoo.elide.core.request.EntityProjection;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

/**
 * {@link DataStoreTransaction} for the {@link OperationDataStore}.
 * <p>
 * This only performs validations on the models and hooks should be configured
 * on the models to perform operations.
 * 
 * @see example.model.Mail
 */
public class OperationDataStoreTransaction implements DataStoreTransaction {
    private final Validator validator;

    public OperationDataStoreTransaction(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public <T> void save(T entity, RequestScope scope) {
    }

    @Override
    public <T> void delete(T entity, RequestScope scope) {
    }

    @Override
    public void flush(RequestScope scope) {
    }

    @Override
    public void commit(RequestScope scope) {
    }

    @Override
    public <T> void createObject(T entity, RequestScope scope) {
        Set<ConstraintViolation<Object>> result = validator.validate(entity);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }

    @Override
    public <T> DataStoreIterable<T> loadObjects(EntityProjection entityProjection, RequestScope scope) {
        return null;
    }

    @Override
    public void cancel(RequestScope scope) {
    }
}
