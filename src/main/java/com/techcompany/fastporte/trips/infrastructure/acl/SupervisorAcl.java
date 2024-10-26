package com.techcompany.fastporte.trips.infrastructure.acl;

import com.techcompany.fastporte.shared.dtos.SupervisorInformationDto;
import com.techcompany.fastporte.shared.utils.EnvironmentConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
public class SupervisorAcl {

    @Value("${internal.api.key}")
    String INTERNAL_API_KEY;

    private final RestTemplate restTemplate;
    private final String BASE_URL = EnvironmentConstants.CURRENT_ENV_URL + "/api/supervisors/";

    public SupervisorAcl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<SupervisorInformationDto> findSupervisorById(Long id) {
        String url = BASE_URL + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-API-Key", INTERNAL_API_KEY);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            SupervisorInformationDto supervisorDto = restTemplate.exchange(url, HttpMethod.GET, entity, SupervisorInformationDto.class).getBody();
            if (supervisorDto != null) {
                return Optional.of(supervisorDto);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<SupervisorInformationDto> findAllSupervisorsByIdIn(List<Long> ids){
        String url = BASE_URL + "batch";
        ResponseEntity<List<SupervisorInformationDto>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(ids),
                new ParameterizedTypeReference<List<SupervisorInformationDto>>() {
                }
        );
        return response.getBody();
    }
}
