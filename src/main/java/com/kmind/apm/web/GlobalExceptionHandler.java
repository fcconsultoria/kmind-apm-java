package com.kmind.apm.web;

import com.kmind.apm.logging.StackTraceParser;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import net.logstash.logback.marker.Markers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger("kmind.error");

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex, HttpServletRequest request) {
        // Cria o payload de resposta (mantendo seu formato atual)
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("status", 500);
        body.put("path", request.getRequestURI());

        Map<String, Object> logData = new HashMap<>();
        logData.put("method", request.getMethod());
        logData.put("url", request.getRequestURL().toString());
        logData.put("error", ex.getMessage());
        logData.put("stack", StackTraceParser.parse(ex.getStackTrace()));

        // Usa Markers para incluir os campos estruturados
        log.error(Markers.appendEntries(logData), "Unhandled request error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}