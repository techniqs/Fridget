package at.ac.tuwien.sepm.fridget.server.suites;

import at.ac.tuwien.sepm.fridget.server.persistence.BillDAOH2Tests;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runners.Suite;
import org.springframework.stereotype.Component;

/**
 * Test suite for all daos. IMPORTANT: Include your test class in the @Suite.SuiteClasses annotation below
 */
@Component
@Suite.SuiteClasses({BillDAOH2Tests.class})
public class DAOSuite extends TestSuiteBase {

    /**
     * Optional: Use this method, or add a separate method annotade with @ClassRule to apply a rule ONCE, before the
     * tests run.
     */
    @ClassRule
    public static ExternalResource resource = new ExternalResource() {

        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }

    };
}
