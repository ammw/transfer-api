package eu.ammw.transfer.domain;

import eu.ammw.transfer.model.Account;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountServiceTest {
    private static final UUID TEST_UUID = UUID.randomUUID();

    private AccountService service = new AccountService();

    @Test
    void shouldGetAccounts() {
        // WHEN
        List<Account> result = service.getAccounts();

        // THEN
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldGetAccountByUUID() {
        // WHEN
        Account result = service.getAccount(TEST_UUID);

        // THEN
        assertThat(result.getId()).isEqualTo(TEST_UUID);
    }

    @Test
    void shouldCreateNewAccount() {
        // WHEN
        Account result = service.createAccount();

        // THEN
        assertThat(result.getId()).isNotNull();
    }
}
