package com.example.campus.controller;

import com.example.campus.service.DashboardService;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }

    @GetMapping("/stats")
    public Map<String, Object> stats() { return service.stats(); }
}
