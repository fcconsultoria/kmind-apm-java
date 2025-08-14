package com.kmind.apm.logging;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Coloca trace_id e span_id no MDC para que o Logback injete no JSON.
 * Também expõe cabeçalhos X-Trace-Id / X-Span-Id.
 */
public class MDCTracingFilter extends OncePerRequestFilter {

    private final OpenTelemetry openTelemetry;

    public MDCTracingFilter(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Span current = Span.current();
            SpanContext ctx = current.getSpanContext();
            String traceId = ctx.isValid() ? ctx.getTraceId() : "";
            String spanId = ctx.isValid() ? ctx.getSpanId() : "";

            MDC.put("trace_id", traceId);
            MDC.put("span_id", spanId);

            if (!traceId.isEmpty()) {
                response.setHeader("X-Trace-Id", traceId);
                response.setHeader("X-Span-Id", spanId);
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("trace_id");
            MDC.remove("span_id");
        }
    }
}
