package at.ac.tuwien.sepm.fridget.server.exception;

import at.ac.tuwien.sepm.fridget.common.exception.InvalidArgumentException;
import at.ac.tuwien.sepm.fridget.server.TestBase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExceptionTests extends TestBase {


    private static final String MESSAGE = "test";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidArgumentExceptionWithExceptionMessage_ShouldReturnInvalidArgumentException() throws InvalidArgumentException {

        thrown.expect(InvalidArgumentException.class);
        thrown.expectMessage(MESSAGE);

        throw new InvalidArgumentException(MESSAGE);

    }

    @Test
    public void testInvalidArgumentExceptionWithThrowable_ShouldReturnInvalidArgumentException() throws InvalidArgumentException {

        thrown.expect(InvalidArgumentException.class);
        thrown.expectMessage(MESSAGE);

        throw new InvalidArgumentException(new InvalidArgumentException(MESSAGE));

    }

    @Test
    public void testInvalidArgumentExceptionWithExceptionMessageAndThrowable_ShouldReturnInvalidArgumentException() throws InvalidArgumentException {

        thrown.expect(InvalidArgumentException.class);
        thrown.expectMessage(MESSAGE);

        throw new InvalidArgumentException(MESSAGE, new InvalidArgumentException(MESSAGE));

    }

    @Override
    public void setUp() throws Exception {

    }

    @Override
    public void tearDown() {

    }
}
