package com.telecom.portabilidades.service;

import com.telecom.portabilidades.dto.PaginaRegistrosDto;
import com.telecom.portabilidades.dto.RegistroApiDto;
import com.telecom.portabilidades.exception.RegistroServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistroClient {

    private static final int PAGE_SIZE = 500;
    private static final ParameterizedTypeReference<PaginaRegistrosDto<RegistroApiDto>> PAGE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public RegistroClient(@Value("${app.registros-service.url}") String serviceUrl) {
        this.restClient = RestClient.builder().baseUrl(serviceUrl).build();
    }

    @Cacheable("registros")
    public List<RegistroApiDto> obtenerTodos() {
        List<RegistroApiDto> records = new ArrayList<>();
        int page = 0;
        try {
            while (true) {
                int currentPage = page;
                PaginaRegistrosDto<RegistroApiDto> response = restClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/api/registros")
                                .queryParam("page", currentPage)
                                .queryParam("size", PAGE_SIZE)
                                .build())
                        .retrieve()
                        .body(PAGE_TYPE);
                if (response == null) {
                    throw new IllegalStateException("El servicio de registros devolvió una respuesta vacía.");
                }
                records.addAll(response.contenido());
                if (response.ultimaPagina()) {
                    return List.copyOf(records);
                }
                page++;
            }
        } catch (RestClientException exception) {
            throw new RegistroServiceUnavailableException(exception);
        }
    }

    public ResponseEntity<String> reenviarConsulta(HttpServletRequest request) {
        String path = UriUtils.decode(request.getRequestURI(), StandardCharsets.UTF_8);
        try {
            return restClient.get()
                    .uri(uriBuilder -> {
                        var target = uriBuilder.path(path);
                        request.getParameterMap().forEach((name, values) ->
                                target.queryParam(name, (Object[]) values));
                        return target.build();
                    })
                    .exchange((clientRequest, clientResponse) -> {
                        String body = StreamUtils.copyToString(clientResponse.getBody(), StandardCharsets.UTF_8);
                        ResponseEntity.BodyBuilder response = ResponseEntity.status(clientResponse.getStatusCode());
                        MediaType contentType = clientResponse.getHeaders().getContentType();
                        if (contentType != null) {
                            response.contentType(contentType);
                        }
                        return response.body(body);
                    });
        } catch (RestClientException exception) {
            throw new RegistroServiceUnavailableException(exception);
        }
    }
}
