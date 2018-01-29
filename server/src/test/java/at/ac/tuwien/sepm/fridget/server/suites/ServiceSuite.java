package at.ac.tuwien.sepm.fridget.server.suites;

import at.ac.tuwien.sepm.fridget.server.services.ServerBillServiceTests;
import at.ac.tuwien.sepm.fridget.server.services.ServerBillShareServiceTests;
import org.junit.runners.Suite;
import org.springframework.stereotype.Component;

/**
 * Test suite for services. IMPORTANT: Include your test class in the @Suite.SuiteClasses annotation below
 */
@Component
@Suite.SuiteClasses({ServerBillServiceTests.class, ServerBillShareServiceTests.class})
public class ServiceSuite extends TestSuiteBase {


}
