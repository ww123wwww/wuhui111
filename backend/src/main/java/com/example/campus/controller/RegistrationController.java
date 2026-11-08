package com.example.campus.controller;

import com.example.campus.dto.ActionResult;
import com.example.campus.dto.Registration;
import com.example.campus.service.RegistrationService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    private final RegistrationService service;
    public RegistrationController(RegistrationService service) { this.service = service; }

    @PostMapping("/activities/{id}/registrations")
    public ActionResult register(@PathVariable long id) { return service.register(id); }

    @DeleteMapping("/activities/{id}/registrations/me")
    public ActionResult cancel(@PathVariable long id) { return service.cancelRegistration(id); }

    @GetMapping("/me/registrations")
    public List<Registration> mine() { return service.mine(); }

    @GetMapping("/admin/activities/{id}/registrations")
    public List<Registration> roster(@PathVariable long id) { return service.roster(id); }
}
