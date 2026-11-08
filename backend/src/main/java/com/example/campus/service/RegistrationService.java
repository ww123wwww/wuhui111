package com.example.campus.service;

import com.example.campus.dto.ActionResult;
import com.example.campus.dto.Registration;
import com.example.campus.exception.AppException;
import com.example.campus.repository.ActivityRepository;
import com.example.campus.repository.RegistrationRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final RegistrationRepository repo;
    private final ActivityRepository activities;
    private final AuthService auth;
    public RegistrationService(RegistrationRepository repo, ActivityRepository activities, AuthService auth) {
        this.repo=repo;this.activities=activities;this.auth=auth;
    }
    private LocalDateTime now() {return LocalDateTime.now(ZoneId.of("Asia/Shanghai"));}
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public ActionResult register(long id) {
        var user=auth.student();
        // 先锁活动，再查询人数与报名记录；所有报名写操作遵循同一顺序。
        activities.lockActivity(id); var a=activities.activity(id); var existing=repo.registration(id,user.id());
        if(!"PUBLISHED".equals(a.status())) throw new AppException(409,"活动未开放报名");
        if(!existing.isEmpty() && "REGISTERED".equals(existing.get(0).get("status"))) return new ActionResult(false,"你已经报名，无需重复操作");
        var time=now();
        if(!a.deadline().isAfter(time)) throw new AppException(409,"报名已经截止");
        if(a.registeredCount()>=a.capacity()) throw new AppException(409,"活动名额已满");
        repo.register(id,user.id(),time,!existing.isEmpty());
        return new ActionResult(true,"报名成功");
    }
    @Transactional(isolation=Isolation.READ_COMMITTED)
    public ActionResult cancelRegistration(long id) {
        var user=auth.student(); activities.lockActivity(id); var a=activities.activity(id);
        var existing=repo.registration(id,user.id());
        if(existing.isEmpty()) throw new AppException(404,"你尚未报名此活动");
        if("CANCELLED".equals(existing.get(0).get("status"))) return new ActionResult(false,"报名已经取消");
        if(!"PUBLISHED".equals(a.status())) throw new AppException(409,"活动已取消，无需取消报名");
        var time=now();
        if(!a.deadline().isAfter(time)) throw new AppException(409,"报名已截止，不能取消");
        repo.cancel(id,user.id(),time); return new ActionResult(true,"已取消报名");
    }
    public List<Registration> mine() { return repo.mine(auth.student().id()); }
    public List<Registration> roster(long id) { auth.admin(); activities.activity(id); return repo.roster(id); }
}
