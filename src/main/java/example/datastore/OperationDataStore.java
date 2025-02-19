package example.datastore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.yahoo.elide.core.datastore.DataStore;
import com.yahoo.elide.core.datastore.DataStoreTransaction;
import com.yahoo.elide.core.dictionary.EntityDictionary;

import jakarta.validation.Validator;

/**
 * {@link DataStore} for operations.
 * <p>
 * This fulfills the same function as Elide's NoopDataStore but uses a Jakarta
 * Bean Validator to validate the entity.
 */
public class OperationDataStore implements DataStore {
    private final Validator validator;
    private final Set<Class<?>> modelsToBind;

    public OperationDataStore(Validator validator, Class<?>... models) {
        this.validator = validator;
        this.modelsToBind = new HashSet<>();
        if (models != null) {
            Collections.addAll(this.modelsToBind, models);
        }
    }

    @Override
    public void populateEntityDictionary(EntityDictionary dictionary) {
        this.modelsToBind.forEach(dictionary::bindEntity);
    }

    @Override
    public DataStoreTransaction beginTransaction() {
        return new OperationDataStoreTransaction(this.validator);
    }
}
