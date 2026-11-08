package com.example.campus.repository;

import com.example.campus.entity.UserAccount;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;
    public UserRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final RowMapper<UserAccount> ROW = (rs, n) -> new UserAccount(
            rs.getLong("id"), rs.getString("username"), rs.getString("display_name"),
            rs.getString("password_hash"), rs.getString("role"));

    public Optional<UserAccount> findByUsername(String username) {
        return jdbc.query("SELECT id,username,display_name,password_hash,role FROM users WHERE username=?",
                ROW, username).stream().findFirst();
    }

    public Optional<UserAccount> findById(long id) {
        return jdbc.query("SELECT id,username,display_name,password_hash,role FROM users WHERE id=?",
                ROW, id).stream().findFirst();
    }
}
