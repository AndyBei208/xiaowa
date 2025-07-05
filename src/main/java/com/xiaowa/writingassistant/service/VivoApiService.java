package com.xiaowa.writingassistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class VivoApiService {
    private final WebClient webClient;
    private final String domain;
    private final String appId;
    private final String appKey;
    private final String modelName;
    private final String completionsUri;

    public VivoApiService(WebClient.Builder webClientBuilder,
                          @Value("${vivo.api.domain}") String domain,
                          @Value("${vivo.api.app-id}") String appId,
                          @Value("${vivo.api.app-key}") String appKey,
                          @Value("${vivo.api.model-name}") String modelName,
                          @Value("${vivo.api.completions-uri}") String completionsUri) {
        this.webClient = webClientBuilder.build();
        this.domain = domain;
        this.appId = appId;
        this.appKey = appKey;
        this.modelName = modelName;
        this.completionsUri = completionsUri;
    }

    public Mono<String> getCompletion(String prompt) {
        try {
            String method = "POST";
            UUID requestId = UUID.randomUUID();
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("requestId", requestId.toString());
            String queryStr = com.xiaowa.writingassistant.utils.MapUtils.mapToQueryString(queryMap);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("prompt", prompt);
            requestBody.put("model", this.modelName);
            requestBody.put("sessionId", UUID.randomUUID().toString());

            HttpHeaders authHeaders = com.xiaowa.writingassistant.utils.VivoAuth.generateAuthHeaders(appId, appKey, method, this.completionsUri, queryStr);
            authHeaders.add("Content-Type", "application/json");

            String url = String.format("http://%s%s?%s", this.domain, this.completionsUri, queryStr);

            return this.webClient.post()
                    .uri(url)
                    .headers(headers -> headers.addAll(authHeaders))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class);

        } catch (Exception e) {
            return Mono.error(new RuntimeException("调用Vivo API时发生异常", e));
        }
    }
}
