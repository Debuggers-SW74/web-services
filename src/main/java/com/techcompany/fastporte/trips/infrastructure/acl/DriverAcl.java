package com.techcompany.fastporte.trips.infrastructure.acl;

import com.techcompany.fastporte.shared.utils.EnvironmentConstants;
import com.techcompany.fastporte.shared.dtos.DriverInformationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class DriverAcl {

    @Value("${internal.api.key}")
    String INTERNAL_API_KEY;

    private final RestTemplate restTemplate;
    private final String BASE_URL = EnvironmentConstants.CURRENT_ENV_URL + "/api/drivers/";

    private static final Logger logger = LoggerFactory.getLogger(DriverAcl.class);

    public DriverAcl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<DriverInformationDto> findDriverById(Long id) {
        String url = BASE_URL + id;
        logger.info("Driver found: " + url);
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-API-Key", INTERNAL_API_KEY);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            DriverInformationDto driverDto = restTemplate.exchange(url, HttpMethod.GET, entity, DriverInformationDto.class).getBody();

            if (driverDto != null) {
                logger.info("Driver found: " + driverDto.firstLastName());
                return Optional.of(driverDto);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error finding driver: " + e.getMessage());
            return Optional.empty();
        }
    }


    public List<DriverInformationDto> findAllDriversByIdIn(List<Long> ids){
        String url = BASE_URL + "batch";
        ResponseEntity<List<DriverInformationDto>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(ids),
                new ParameterizedTypeReference<List<DriverInformationDto>>() {}
        );
        return response.getBody();
    }
}
