package com.kmind.apm;

import com.kmind.apm.config.OpenTelemetryConfig;
import com.kmind.apm.logging.MDCTracingFilter;
import com.kmind.apm.middleware.RequestLoggingFilter;
import com.kmind.apm.middleware.NotFoundHandler;
import com.kmind.apm.web.GlobalExceptionHandler;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
public class KmindApmAutoConfiguration {

    @Bean
    public OpenTelemetry openTelemetry() {
        return OpenTelemetryConfig.buildOpenTelemetry();
    }

    @Bean
    public Tracer tracer(OpenTelemetry otel) {
        return otel.getTracer("kmind-apm");
    }

    @Bean
    public FilterRegistrationBean<MDCTracingFilter> mdcTracingFilter(OpenTelemetry otel) {
        FilterRegistrationBean<MDCTracingFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new MDCTracingFilter(otel));
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new RequestLoggingFilter());
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return reg;
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() { return new GlobalExceptionHandler(); }

    @Bean
    public NotFoundHandler notFoundHandler() { return new NotFoundHandler(); }
}
