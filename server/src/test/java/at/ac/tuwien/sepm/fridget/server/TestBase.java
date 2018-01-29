package at.ac.tuwien.sepm.fridget.server;

import at.ac.tuwien.sepm.fridget.server.application.FridgetServer;
import at.ac.tuwien.sepm.fridget.server.configuration.TestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ResourceBundle;

/**
 * Abstract base class for all test classes
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FridgetServer.class, TestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("unit-test")
public abstract class TestBase {

    /**
     * Properties file for testing
     */
    protected static ResourceBundle properties = ResourceBundle.getBundle("testing");

    /**
     * Called before the first test in a test class (e.g. GreetingControllerTests.java) is executed
     */
    public static void setUpClass() throws Exception {
        // Override me in test implementations (if needed)
    }

    /**
     * Called after test class execution (e.g. when running GreetingControllerTests.java), so when all tests inside a
     * test class are done
     */
    public static void tearDownClass() {
        // Override me in test implementations (if needed)
    }

    /**
     * Called before every test
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * Called after every test
     */
    @After
    public void tearDown() {}
}
