package at.ac.tuwien.sepm.fridget.server.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for test suites. Test suites are used to group similar tests (e.g. tests that access the database) into
 * one container. Extend this class and define the required fields to create suites as necessary.
 * For implemenntation @see at.ac.tuwien.sepm.fridget.server.suites.DAOMemorySuite
 */
@RunWith(Suite.class)
@ActiveProfiles("unit-test")
public abstract class TestSuiteBase {
}
