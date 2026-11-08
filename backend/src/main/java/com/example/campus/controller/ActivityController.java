package com.example.campus.controller;

import com.example.campus.dto.Activity;
import com.example.campus.dto.ActivityForm;
import com.example.campus.service.ActivityService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ActivityController {
    private final ActivityService service;
    public ActivityController(ActivityService service) { this.service = service; }

    @GetMapping("/activities")
    public List<Activity> list(@RequestParam(defaultValue="") String keyword) { return service.list(keyword); }

    @GetMapping("/activities/{id}")
    public Activity detail(@PathVariable long id) { return service.detail(id); }

    @PostMapping("/admin/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> create(@Valid @RequestBody ActivityForm form) { return Map.of("id", service.create(form)); }

    @PutMapping("/admin/activities/{id}")
    public Map<String, String> edit(@PathVariable long id, @Valid @RequestBody ActivityForm form) {
        service.edit(id, form); return ok();
    }

    @DeleteMapping("/admin/activities/{id}")
    public Map<String, String> delete(@PathVariable long id) { service.delete(id); return ok(); }

    @PostMapping("/admin/activities/{id}/publish")
    public Map<String, String> publish(@PathVariable long id) { service.publish(id); return ok(); }

    @PostMapping("/admin/activities/{id}/cancel")
    public Map<String, String> cancel(@PathVariable long id) { service.cancelActivity(id); return ok(); }

    private Map<String, String> ok() { return Map.of("message", "操作成功"); }
}
