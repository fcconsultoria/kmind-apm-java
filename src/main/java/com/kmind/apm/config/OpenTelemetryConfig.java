package com.kmind.apm.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenTelemetryConfig {

    @Value("${OTEL_ENABLE_TRACE:true}")
    private boolean enableTracing;

    @Value("${OTEL_TENANT_ID:unknown}")
    private String tenantId;

    @Value("${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4318}")
    private String otlpEndpoint;

    @Value("${OTEL_SERVICE_NAME:unknown-service}")
    private String serviceName;

    @Value("${OTEL_CLUSTER_NAME:unknown-cluster}")
    private String clusterName;

    @Value("${OTEL_CONTAINER_NAME:unknown-container}")
    private String containerName;

    public static OpenTelemetry buildOpenTelemetry(
            boolean enableTracing,
            String tenantId,
            String otlpEndpoint,
            String serviceName,
            String clusterName,
            String containerName) {
        
        if (!enableTracing) {
            return OpenTelemetry.noop();
        }

        Resource resource = Resource.getDefault()
            .merge(Resource.create(Attributes.of(
                ResourceAttributes.SERVICE_NAME, serviceName,
                ResourceAttributes.K8S_CLUSTER_NAME, clusterName,
                ResourceAttributes.CONTAINER_NAME, containerName
            )));

        OtlpHttpSpanExporter spanExporter = OtlpHttpSpanExporter.builder()
            .setEndpoint(otlpEndpoint)
            .addHeader("X-Scope-OrgId", tenantId)
            .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .setResource(resource)
            .build();

        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .buildAndRegisterGlobal();
    }

    @Bean
    public OpenTelemetry openTelemetry() {
        return buildOpenTelemetry(
            enableTracing,
            tenantId,
            otlpEndpoint,
            serviceName,
            clusterName,
            containerName
        );
    }
}