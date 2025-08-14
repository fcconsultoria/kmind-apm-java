package com.kmind.apm.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;

public final class OpenTelemetryConfig {
    private OpenTelemetryConfig() {}

    public static OpenTelemetry buildOpenTelemetry() {
        String endpoint = System.getenv().getOrDefault("OTEL_EXPORTER_OTLP_ENDPOINT", "http://otel-collector:4317");
        String serviceName = System.getenv().getOrDefault("OTEL_SERVICE_NAME", "kmind-java-service");
        String clusterName = System.getenv().getOrDefault("OTEL_CLUSTER_NAME", "kmind-java-cluster");
        String containerName = System.getenv().getOrDefault("OTEL_CONTAINER_NAME", "kmind-java-container");

        OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(endpoint)
                .build();

        Resource resource = Resource.getDefault().merge(Resource.create(
                Attributes.builder()
                .put("service.name", serviceName)
                .put("cluster", clusterName)
                .put("container", containerName).build()
        ));

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .setResource(resource)
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }
}