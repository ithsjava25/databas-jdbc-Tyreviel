package com.example;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findByNameAndPassword(String name, String password);
    Account create(Account account);
    boolean updatePassword(int userId, String newPassword);
    boolean delete(int userId);
}
