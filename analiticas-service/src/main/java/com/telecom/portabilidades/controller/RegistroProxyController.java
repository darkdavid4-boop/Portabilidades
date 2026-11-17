package com.telecom.portabilidades.controller;

import com.telecom.portabilidades.service.RegistroClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistroProxyController {

    private final RegistroClient registroClient;

    public RegistroProxyController(RegistroClient registroClient) {
        this.registroClient = registroClient;
    }

    @GetMapping({"/api/registros", "/api/registros/**"})
    public ResponseEntity<String> reenviarConsulta(HttpServletRequest request) {
        return registroClient.reenviarConsulta(request);
    }
}
