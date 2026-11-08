package com.example.campus.service;

import com.example.campus.dto.Activity;
import com.example.campus.dto.ActivityForm;
import com.example.campus.exception.AppException;
import com.example.campus.repository.ActivityRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {
    private final ActivityRepository repo;
    private final AuthService auth;
    public ActivityService(ActivityRepository repo, AuthService auth) {this.repo=repo;this.auth=auth;}
    private LocalDateTime now() { return LocalDateTime.now(ZoneId.of("Asia/Shanghai")); }
    public List<Activity> list(String keyword) {
        var user=auth.current();
        if(keyword.length()>100) throw new AppException(400,"搜索内容过长");
        return repo.activities("ADMIN".equals(user.role()),keyword.strip());
    }
    public Activity detail(long id) {
        var user=auth.current(); var a=repo.activity(id);
        if ("DRAFT".equals(a.status()) && !"ADMIN".equals(user.role())) throw new AppException(404,"活动不存在");
        return a;
    }
    private void validate(ActivityForm f) {
        if(!f.startTime().isAfter(now())) throw new AppException(400,"开始时间必须晚于现在");
        if(!f.endTime().isAfter(f.startTime())) throw new AppException(400,"结束时间必须晚于开始时间");
        if(!f.deadline().isAfter(now()) || f.deadline().isAfter(f.startTime())) throw new AppException(400,"截止时间须晚于现在且不晚于开始时间");
    }
    @Transactional public long create(ActivityForm f) { auth.admin(); validate(f); return repo.create(f); }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void edit(long id, ActivityForm f) {
        auth.admin(); repo.lockActivity(id); var a=repo.activity(id);
        if(!"DRAFT".equals(a.status())) throw new AppException(409,"只有草稿可以修改");
        validate(f); repo.update(id,f);
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void delete(long id) {
        auth.admin(); repo.lockActivity(id);
        if(!"DRAFT".equals(repo.activity(id).status())) throw new AppException(409,"只有草稿可以删除");
        repo.delete(id);
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void publish(long id) {
        auth.admin(); repo.lockActivity(id); var a=repo.activity(id);
        if(!"DRAFT".equals(a.status())) throw new AppException(409,"只有草稿可以发布");
        if(!a.deadline().isAfter(now())) throw new AppException(409,"报名截止时间已过，请先修改草稿");
        repo.status(id,"PUBLISHED");
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void cancelActivity(long id) {
        auth.admin(); repo.lockActivity(id); var a=repo.activity(id);
        if("CANCELLED".equals(a.status())) return;
        if(!"PUBLISHED".equals(a.status())) throw new AppException(409,"只能取消已发布活动");
        if(!a.startTime().isAfter(now())) throw new AppException(409,"活动已经开始，不能取消");
        repo.status(id,"CANCELLED");
    }
}
