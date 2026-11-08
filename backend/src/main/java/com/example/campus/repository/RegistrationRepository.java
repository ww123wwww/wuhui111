package com.example.campus.repository;

import com.example.campus.dto.Registration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RegistrationRepository {
    private final JdbcTemplate jdbc;
    public RegistrationRepository(JdbcTemplate jdbc) {this.jdbc=jdbc;}
    public List<Map<String,Object>> registration(long activityId, long userId) {
        return jdbc.queryForList("SELECT id,status FROM registrations WHERE activity_id=? AND user_id=?",activityId,userId);
    }
    public void register(long activityId, long userId, LocalDateTime now, boolean existing) {
        if (existing) jdbc.update("UPDATE registrations SET status='REGISTERED',registered_at=?,cancelled_at=NULL WHERE activity_id=? AND user_id=?",now,activityId,userId);
        else jdbc.update("INSERT INTO registrations(activity_id,user_id,status,registered_at) VALUES(?,?,'REGISTERED',?)",activityId,userId,now);
    }
    public void cancel(long activityId, long userId, LocalDateTime now) {
        jdbc.update("UPDATE registrations SET status='CANCELLED',cancelled_at=? WHERE activity_id=? AND user_id=?",now,activityId,userId);
    }
    private static final String REG_SELECT = """
        SELECT r.*,u.display_name,u.username,a.title,a.location,a.start_time,a.status AS activity_status
        FROM registrations r JOIN users u ON r.user_id=u.id JOIN activities a ON r.activity_id=a.id
        """;
    private static final RowMapper<Registration> REG_ROW = (rs,n) -> new Registration(
        rs.getLong("id"),rs.getLong("activity_id"),rs.getLong("user_id"),rs.getString("display_name"),
        rs.getString("username"),rs.getString("title"),rs.getString("location"),
        rs.getTimestamp("start_time").toLocalDateTime(),rs.getString("activity_status"),
        rs.getString("status"),rs.getTimestamp("registered_at").toLocalDateTime());
    public List<Registration> mine(long userId) {
        return jdbc.query(REG_SELECT+" WHERE r.user_id=? ORDER BY r.registered_at DESC,r.id DESC",REG_ROW,userId);
    }
    public List<Registration> roster(long activityId) {
        return jdbc.query(REG_SELECT+" WHERE r.activity_id=? ORDER BY r.id",REG_ROW,activityId);
    }
}
