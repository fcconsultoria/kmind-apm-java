package com.kmind.apm.config;

// import io.opentelemetry.api.OpenTelemetry;
// import io.opentelemetry.api.common.Attributes;
// import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
// import io.opentelemetry.context.propagation.ContextPropagators;
// import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
// import io.opentelemetry.sdk.OpenTelemetrySdk;
// import io.opentelemetry.sdk.resources.Resource;
// import io.opentelemetry.sdk.trace.SdkTracerProvider;
// import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
// import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import jakarta.annotation.PostConstruct;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.util.Collections;

// @Configuration
// public class OpenTelemetryConfig {
//     private static final Logger logger = LoggerFactory.getLogger(OpenTelemetryConfig.class);

//     @Value("${OTEL_ENABLE_TRACE:true}")
//     private boolean enableTracing;

//     @Value("${OTEL_TENANT_ID:unknown}")
//     private String tenantId;

//     @Value("${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4318}")
//     private String otlpEndpoint;

//     @Value("${OTEL_SERVICE_NAME:unknown-service}")
//     private String serviceName;

//     @Value("${OTEL_CLUSTER_NAME:unknown-cluster}")
//     private String clusterName;

//     @Value("${OTEL_CONTAINER_NAME:unknown-container}")
//     private String containerName;

//     @PostConstruct
//     public void init() {
//         logger.info("===========================================");
//         logger.info("Configuração OpenTelemetry Iniciada");
//         logger.info("Tracing habilitado: {}", enableTracing);
//         logger.info("OTLP Endpoint: {}", otlpEndpoint);
//         logger.info("Service Name: {}", serviceName);
//         logger.info("===========================================");
//     }

//     public static OpenTelemetry buildOpenTelemetry(
//             boolean enableTracing,
//             String tenantId,
//             String otlpEndpoint,
//             String serviceName,
//             String clusterName,
//             String containerName) {
        
        
//         if (!enableTracing) {
//             System.out.println("Tracing DESABILITADO via configuração");
//             return OpenTelemetry.noop();
//         }

//         System.out.println("[FALLBACK LOG] Configurando OpenTelemetry. enableTracing={} path={} message={}");
//         System.out.println(otlpEndpoint);
//         System.out.println(tenantId);

//         try {
//             Resource resource = Resource.getDefault()
//                 .merge(Resource.create(
//                     Attributes.builder()
//                     .put("service.name", serviceName)
//                     .put("k8s.cluster.name", clusterName)
//                     .put("container.name", containerName)
//                     .build()));

//             OtlpHttpSpanExporter spanExporter = OtlpHttpSpanExporter.builder()
//                 .setEndpoint(otlpEndpoint)
//                 .addHeader("X-Scope-OrgId", tenantId)
//                 .setTimeout(java.time.Duration.ofSeconds(10))
//                 .build();
            
//             System.out.println("Configurando exportador para: {}");
//             System.out.println(otlpEndpoint);

//             SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
//                 .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
//                 .setResource(resource)
//                 .build();

//             OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
//                 .setTracerProvider(tracerProvider)
//                 .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
//                 .buildAndRegisterGlobal();

//             System.out.println("OpenTelemetry configurado com sucesso");
//             return sdk;

//         } catch (Exception e) {
//             System.out.println("Falha ao configurar OpenTelemetry");
//             System.out.println(e);
//             return OpenTelemetry.noop();
//         }
//     }

//     @Bean
//     public OpenTelemetry openTelemetry() {
//         return buildOpenTelemetry(
//             enableTracing,
//             tenantId,
//             otlpEndpoint,
//             serviceName,
//             clusterName,
//             containerName
//         );
//     }
// }

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${otel.service.name:unknown-service}")
    private String serviceName;

    @Value("${otel.cluster.name:unknown-cluster}")
    private String clusterName;

    @Value("${otel.container.name:unknown-container}")
    private String containerName;

    @Bean
    public OpenTelemetry openTelemetry() {
        
        System.out.println("[FALLBACK LOG] Define atributos customizados (cluster, container, etc.)");
        Resource resource = Resource.getDefault()
            .merge(Resource.create(
                Attributes.builder()
                    .put("service.name", serviceName)
                    .put("cluster", clusterName)
                    .put("container", containerName)
                    .build()
            ));

        // 2. Configura o SDK do OpenTelemetry
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .setResource(resource)
            .build();

        // 3. Constrói e retorna a instância customizada
        return OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();
    }
}