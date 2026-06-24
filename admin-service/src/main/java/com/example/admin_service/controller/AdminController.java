package com.example.admin_service.controller;

import com.example.admin_service.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminNotificationService adminNotificationService;

    @PostMapping("/notifications")
    public ResponseEntity<String> sendAdminNotification(@RequestBody String message) {
        return ResponseEntity.ok(adminNotificationService.sendAdminNotification(message));
    }
}
