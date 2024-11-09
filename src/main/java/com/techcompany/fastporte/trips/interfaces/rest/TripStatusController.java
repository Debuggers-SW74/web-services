package com.techcompany.fastporte.trips.interfaces.rest;

import com.techcompany.fastporte.trips.domain.model.aggregates.entities.TripStatus;
import com.techcompany.fastporte.trips.domain.model.queries.GetAllTripStatusQuery;
import com.techcompany.fastporte.trips.domain.services.TripStatusQueryService;
import com.techcompany.fastporte.trips.interfaces.rest.resources.TripStatusResource;
import com.techcompany.fastporte.trips.interfaces.rest.transform.fromEntity.TripStatusResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/trip-status")
public class TripStatusController {

    private final TripStatusQueryService tripStatusQueryService;

    public TripStatusController(TripStatusQueryService tripStatusQueryService) {
        this.tripStatusQueryService = tripStatusQueryService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripStatusResource>> getAllTripStatus() {
        try{
            List<TripStatus> tripStatusList = tripStatusQueryService.handle(new GetAllTripStatusQuery());

            if (tripStatusList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                var tripStatusResourceList = tripStatusList.stream()
                        .map(TripStatusResourceFromEntityAssembler::fromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(tripStatusResourceList);
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
}