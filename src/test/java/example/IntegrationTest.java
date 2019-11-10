package example;

import com.yahoo.elide.standalone.ElideStandalone;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Base class for running a set of functional Elide tests.  This class
 * sets up an Elide instance with an in-memory H2 database.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationTest {
    private ElideStandalone elide;

    @BeforeAll
    public void init() throws Exception {
        Settings settings = new Settings(true) {
            @Override
            public int getPort() {
                return 8080;
            }
        };

        elide = new ElideStandalone(settings);

        settings.runLiquibaseMigrations();

        elide.start(false);
    }

    @AfterAll
    public void shutdown() throws Exception {
        elide.stop();
    }
}
