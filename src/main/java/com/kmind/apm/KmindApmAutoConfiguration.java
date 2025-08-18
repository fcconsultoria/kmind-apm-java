package com.kmind.apm;

// import com.kmind.apm.config.OpenTelemetryConfig;
import com.kmind.apm.logging.MDCTracingFilter;
import com.kmind.apm.middleware.RequestLoggingFilter;
import com.kmind.apm.middleware.NotFoundHandler;
import com.kmind.apm.web.GlobalExceptionHandler;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
// import io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration
@ConditionalOnWebApplication
@PropertySource("classpath:application.properties")
public class KmindApmAutoConfiguration {

    // @Bean
    // public OpenTelemetry openTelemetry(Environment env) {
    //     return OpenTelemetryConfig.build(env);
    // }

    // @Bean
    // @ConditionalOnMissingBean
    // @ConditionalOnProperty(name = "OTEL_ENABLE_TRACE", havingValue = "true", matchIfMissing = true)
    // public OpenTelemetry openTelemetry() {
    //     System.out.println("[FALLBACK LOG] Iniciando configuração OpenTelemetry");
    //     // OpenTelemetry otel = OpenTelemetryConfig.buildOpenTelemetry(
    //     //     true, // enableTracing
    //     //     System.getenv().getOrDefault("OTEL_TENANT_ID", "unknown"),
    //     //     System.getenv().getOrDefault("OTEL_EXPORTER_OTLP_ENDPOINT", "http://localhost:4318"),
    //     //     System.getenv().getOrDefault("OTEL_SERVICE_NAME", "unknown-service"),
    //     //     System.getenv().getOrDefault("OTEL_CLUSTER_NAME", "unknown-cluster"),
    //     //     System.getenv().getOrDefault("OTEL_CONTAINER_NAME", "unknown-container")
    //     // );

    //     OpenTelemetry otel = OpenTelemetryConfig.buildOpenTelemetry();

    //     System.out.println("[FALLBACK LOG] Configuração OpenTelemetry concluída");

    //     return otel;
    // }

    @Bean
    @ConditionalOnMissingBean
    public Tracer tracer(OpenTelemetry otel) {
        return otel.getTracer("kmind-apm");
    }

    @Bean
    @ConditionalOnProperty(name = "OTEL_ENABLE_TRACE", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<MDCTracingFilter> mdcTracingFilter(OpenTelemetry openTelemetry) {
        FilterRegistrationBean<MDCTracingFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new MDCTracingFilter(openTelemetry));
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