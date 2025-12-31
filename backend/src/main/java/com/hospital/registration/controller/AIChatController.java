package com.hospital.registration.controller;

import com.hospital.registration.service.AIChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");

        if (message == null || message.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("data", null);
            error.put("error", Map.of("message", "消息不能为空"));
            return ResponseEntity.badRequest().body(error);
        }

        String reply = aiChatService.chat(message.trim(), history);

        // 返回符合前端拦截器期望的格式: { success: true, data: reply }
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", reply);

        return ResponseEntity.ok(response);
    }
}
