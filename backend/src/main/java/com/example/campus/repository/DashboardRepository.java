package com.example.campus.repository;

import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {
    private final JdbcTemplate jdbc;
    public DashboardRepository(JdbcTemplate jdbc) {this.jdbc=jdbc;}
    public Map<String,Object> stats() {
        return Map.of("activities", jdbc.queryForObject("SELECT COUNT(*) FROM activities",Long.class),
            "published",jdbc.queryForObject("SELECT COUNT(*) FROM activities WHERE status='PUBLISHED'",Long.class),
            "students",jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE role='STUDENT'",Long.class),
            "registrations",jdbc.queryForObject("SELECT COUNT(*) FROM registrations r JOIN activities a ON a.id=r.activity_id WHERE r.status='REGISTERED' AND a.status='PUBLISHED'",Long.class));
    }
}
