package example.datastore;

import com.yahoo.elide.core.datastore.DataStore;
import com.yahoo.elide.core.datastore.DataStoreTransaction;
import com.yahoo.elide.core.dictionary.EntityDictionary;

import example.service.QueryService;
import example.service.SpringDataCursorEncoder;

/**
 * {@link DataStore} for Spring Data that supports both Offset and Cursor Pagination.
 * <p>
 * This is for demonstration purposes only.
 */
public class SpringDataDataStore implements DataStore {
    private QueryService queryByCursorService;
    private SpringDataCursorEncoder cursorEncoder;

    public SpringDataDataStore(QueryService queryByCursorService, SpringDataCursorEncoder cursorEncoder) {
        this.queryByCursorService = queryByCursorService;
        this.cursorEncoder = cursorEncoder;
    }

    @Override
    public void populateEntityDictionary(EntityDictionary dictionary) {
        queryByCursorService.getEntities().forEach(dictionary::bindEntity);
    }

    @Override
    public DataStoreTransaction beginTransaction() {
        return new SpringDataDataStoreTransaction(this.queryByCursorService, this.cursorEncoder);
    }
}
