package at.ac.tuwien.sepm.fridget.common.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordHelpersTests {

    private static final String PASSWORD_PLAIN = "fridgettest";
    private static final String PASSWORD_CLIENT = "27d5b254f7c6efe5180acd7ddc86d9b831c72706f4beff2ae40bf4764b90e41f";
    private static final String PASSWORD_CLIENT_BAD = "57d5b254f7c6efe5180acd7ddc86d9b831c72706f4beff2ae40bf4764b90e41f";
    private static final String PASSWORD_STORED = "$2a$11$EKjRpNAAws8hd/gkyQiqkOoiqaaNGALXRUXxcaSzxo9cuB8lCfK5i";

    @Test
    public void hashOnClient_ShouldReturnValidHash() {
        assertThat(PasswordHelpers.hashOnClient(PASSWORD_PLAIN)).isEqualTo(PASSWORD_CLIENT);
    }

    @Test
    public void hashForStorage_ShouldReturnDifferentHashes() {
        String firstHash = PasswordHelpers.hashForStorage(PASSWORD_CLIENT);
        String secondHash = PasswordHelpers.hashForStorage(PASSWORD_CLIENT);
        assertThat(firstHash).isNotEqualTo(secondHash);
    }

    @Test
    public void hashForStorage_ShouldReturnValidHashes() {
        String storedHash = PasswordHelpers.hashForStorage(PASSWORD_CLIENT);
        assertThat(PasswordHelpers.compareWithDatabase(storedHash, PASSWORD_CLIENT)).isTrue();
        assertThat(PasswordHelpers.compareWithDatabase(storedHash, PASSWORD_CLIENT_BAD)).isFalse();
    }

    @Test
    public void compareWithDatabase_ShouldWorkWithValidData() {
        assertThat(PasswordHelpers.compareWithDatabase(PASSWORD_STORED, PASSWORD_CLIENT)).isTrue();
    }

    @Test
    public void compareWithDatabase_ShouldFailWithInvalidData() {
        assertThat(PasswordHelpers.compareWithDatabase(PASSWORD_STORED, PASSWORD_CLIENT_BAD)).isFalse();
    }

}
