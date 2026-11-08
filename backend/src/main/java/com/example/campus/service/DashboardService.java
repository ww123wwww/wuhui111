package com.example.campus.service;

import com.example.campus.repository.DashboardRepository;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final DashboardRepository repo;
    private final AuthService auth;
    public DashboardService(DashboardRepository repo, AuthService auth) {this.repo=repo;this.auth=auth;}
    public Map<String,Object> stats() {auth.admin();return repo.stats();}
}
