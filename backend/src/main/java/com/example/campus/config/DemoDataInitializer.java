package com.example.campus.config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 仅空库第一次启动时插入演示数据。重启不会覆盖已有数据。 */
@Component
@ConditionalOnProperty(name="demo.seed",havingValue="true",matchIfMissing=true)
public class DemoDataInitializer implements CommandLineRunner {
    private final JdbcTemplate jdbc;
    public DemoDataInitializer(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    @Override @Transactional public void run(String... args) {
        if(jdbc.queryForObject("SELECT COUNT(*) FROM users",Integer.class)>0) return;
        var encoder=new BCryptPasswordEncoder();
        jdbc.update("INSERT INTO users(username,display_name,password_hash,role) VALUES(?,?,?,?)","admin","校园管理员",encoder.encode("Admin123!"),"ADMIN");
        jdbc.update("INSERT INTO users(username,display_name,password_hash,role) VALUES(?,?,?,?)","student","陈同学",encoder.encode("Student123!"),"STUDENT");
        jdbc.update("INSERT INTO users(username,display_name,password_hash,role) VALUES(?,?,?,?)","student2","林同学",encoder.encode("Student123!"),"STUDENT");
        var now=LocalDateTime.now(ZoneId.of("Asia/Shanghai")).withSecond(0).withNano(0);
        String[] titles={"Java 后端开发分享会","校园摄影漫步","软件工程项目交流"};
        String[] locations={"图书馆报告厅","校园东门集合","教学楼 A302"};
        for(int i=0;i<titles.length;i++) {
            var start=now.plusDays(7+i);
            jdbc.update("INSERT INTO activities(title,description,location,start_time,end_time,deadline,capacity,status) VALUES(?,?,?,?,?,?,?,?)",
                titles[i],"欢迎参加校园交流活动。请在报名截止前报名，并按时到场。",locations[i],start,start.plusHours(2),start.minusDays(1),30+i*10,i==2?"DRAFT":"PUBLISHED");
        }
    }
}
