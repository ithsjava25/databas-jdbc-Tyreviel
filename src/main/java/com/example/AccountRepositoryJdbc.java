package com.example;


import com.example.SimpleDriverManagerDataSource;

import java.sql.*;
import java.util.Optional;

public class AccountRepositoryJdbc implements AccountRepository {
    private final SimpleDriverManagerDataSource ds;

    public AccountRepositoryJdbc(SimpleDriverManagerDataSource ds) {
        this.ds = ds;
    }

    @Override
    public Optional<Account> findByNameAndPassword(String name, String password) {
        String sql = "SELECT * FROM account WHERE name = ? AND password = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("ssn")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Account create(Account account) {
        String sql = "INSERT INTO account (name, password, first_name, last_name, ssn) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.name());
            ps.setString(2, account.password());
            ps.setString(3, account.firstName());
            ps.setString(4, account.lastName());
            ps.setString(5, account.ssn());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Account(keys.getInt(1), account.name(), account.password(),
                            account.firstName(), account.lastName(), account.ssn());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    @Override
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE account SET password = ? WHERE user_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int userId) {
        String sql = "DELETE FROM account WHERE user_id = ?";
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
