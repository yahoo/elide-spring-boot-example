package example.datastore;

import com.yahoo.elide.datastores.jpa.transaction.NonJtaTransaction;

import javax.persistence.EntityManager;
import java.io.IOException;

/**
 * Wrapper around Elide's JPA Transaction class which closes the entity manager.
 */
public class SpringDataTransaction extends NonJtaTransaction {
    public SpringDataTransaction(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public void close() throws IOException {
        super.close();
        em.close();
    }
}
