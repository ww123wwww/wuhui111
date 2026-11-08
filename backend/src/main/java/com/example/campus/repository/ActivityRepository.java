package com.example.campus.repository;

import com.example.campus.dto.Activity;
import com.example.campus.dto.ActivityForm;
import com.example.campus.exception.AppException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityRepository {
    private final JdbcTemplate jdbc;
    public ActivityRepository(JdbcTemplate jdbc) {this.jdbc=jdbc;}
    private static final String ACTIVITY_SELECT = """
        SELECT a.*, (SELECT COUNT(*) FROM registrations r
          WHERE r.activity_id=a.id AND r.status='REGISTERED') AS registered_count
        FROM activities a
        """;
    private static final RowMapper<Activity> ACTIVITY_ROW = (rs, n) -> new Activity(
        rs.getLong("id"), rs.getString("title"), rs.getString("description"), rs.getString("location"),
        rs.getTimestamp("start_time").toLocalDateTime(), rs.getTimestamp("end_time").toLocalDateTime(),
        rs.getTimestamp("deadline").toLocalDateTime(), rs.getInt("capacity"),
        rs.getString("status"), rs.getInt("registered_count"));

    public List<Activity> activities(boolean admin, String keyword) {
        // 参数绑定，不把用户输入拼接成 SQL。LOCATE 将 %、_ 当作普通文字。
        return jdbc.query(ACTIVITY_SELECT + " WHERE " + (admin ? "1=1" : "a.status<>'DRAFT'")
                + " AND LOCATE(?, a.title)>0 ORDER BY a.id DESC", ACTIVITY_ROW, keyword);
    }
    public Activity activity(long id) {
        var rows = jdbc.query(ACTIVITY_SELECT + " WHERE a.id=?", ACTIVITY_ROW, id);
        if (rows.isEmpty()) throw new AppException(404, "活动不存在");
        return rows.get(0);
    }
    /** 写操作先锁同一活动行。锁必须在 Service 事务内持有。 */
    public void lockActivity(long id) {
        var rows = jdbc.queryForList("SELECT id FROM activities WHERE id=? FOR UPDATE", id);
        if (rows.isEmpty()) throw new AppException(404, "活动不存在");
    }
    public long create(ActivityForm f) {
        var keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement("""
                INSERT INTO activities(title,description,location,start_time,end_time,deadline,capacity,status)
                VALUES(?,?,?,?,?,?,?,'DRAFT')
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,f.title().strip()); ps.setString(2,f.description().strip());
            ps.setString(3,f.location().strip()); ps.setTimestamp(4,Timestamp.valueOf(f.startTime()));
            ps.setTimestamp(5,Timestamp.valueOf(f.endTime())); ps.setTimestamp(6,Timestamp.valueOf(f.deadline()));
            ps.setInt(7,f.capacity()); return ps;
        }, keys);
        return keys.getKey().longValue();
    }
    public void update(long id, ActivityForm f) {
        jdbc.update("""
            UPDATE activities SET title=?,description=?,location=?,start_time=?,end_time=?,deadline=?,capacity=? WHERE id=?
            """, f.title().strip(), f.description().strip(), f.location().strip(),
                f.startTime(), f.endTime(), f.deadline(), f.capacity(), id);
    }
    public void status(long id, String status) { jdbc.update("UPDATE activities SET status=? WHERE id=?",status,id); }
    public void delete(long id) { jdbc.update("DELETE FROM activities WHERE id=?", id); }
}
