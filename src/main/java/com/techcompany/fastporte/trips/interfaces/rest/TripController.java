package com.techcompany.fastporte.trips.interfaces.rest;

import com.techcompany.fastporte.trips.application.dtos.TripCreatedDto;
import com.techcompany.fastporte.trips.domain.model.aggregates.entities.Trip;
import com.techcompany.fastporte.trips.domain.model.commands.DeleteTripCommand;
import com.techcompany.fastporte.trips.domain.model.queries.*;
import com.techcompany.fastporte.trips.domain.services.TripCommandService;
import com.techcompany.fastporte.trips.domain.services.TripQueryService;
import com.techcompany.fastporte.trips.interfaces.rest.resources.CreateTripResource;
import com.techcompany.fastporte.trips.interfaces.rest.resources.TripInformationResource;
import com.techcompany.fastporte.trips.interfaces.rest.transform.fromEntity.TripInformationResourceFromEntityAssembler;
import com.techcompany.fastporte.trips.interfaces.rest.transform.fromResource.CreateTripCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripCommandService tripCommandService;
    private final TripQueryService tripQueryService;

    public TripController(TripCommandService tripCommandService, TripQueryService tripQueryService) {
        this.tripCommandService = tripCommandService;
        this.tripQueryService = tripQueryService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripInformationResource> findById(@PathVariable Long id) {
        try {
            Optional<Trip> trip = tripQueryService.handle(new GetTripByIdQuery(id));

            if (trip.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(TripInformationResourceFromEntityAssembler.toResourceFromEntity(trip.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripInformationResource>> findAll() {
        try {
            List<Trip> trips = tripQueryService.handle(new GetAllTripsQuery());

            if (trips.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                var tripInformationResources = trips.stream()
                        .map(TripInformationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(tripInformationResources);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/supervisor/{supervisorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripInformationResource>> findBySupervisorId(@PathVariable Long supervisorId) {
        try {
            List<Trip> trips = tripQueryService.handle(new GetTripsBySupervisorIdQuery(supervisorId));

            if (trips.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                var tripInformationResources = trips.stream()
                        .map(TripInformationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(tripInformationResources);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripInformationResource>> findByDriverId(@PathVariable Long driverId) {
        try {
            List<Trip> trips = tripQueryService.handle(new GetTripsByDriverIdQuery(driverId));

            if (trips.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                var tripInformationResources = trips.stream()
                        .map(TripInformationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(tripInformationResources);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/driver/{driverId}/status/{statusId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripInformationResource>> findByDriverIdAndStatus(@PathVariable Long driverId, @PathVariable Long statusId) {
        try {
            List<Trip> trips = tripQueryService.handle(new GetTripsByDriverIdAndStatusQuery(driverId, statusId));

            if (trips.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                var tripInformationResources = trips.stream()
                        .map(TripInformationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(tripInformationResources);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/supervisor/{supervisorId}/status/{statusId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TripInformationResource>> findBySupervisorIdAndStatus(@PathVariable Long supervisorId, @PathVariable Long statusId) {
        try {
            List<Trip> trips = tripQueryService.handle(new GetTripsBySupervisorIdAndStatusQuery(supervisorId, statusId));

            if (trips.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                var tripInformationResources = trips.stream()
                        .map(TripInformationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(tripInformationResources);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TripInformationResource> save(@RequestBody CreateTripResource resource) {
        try {
            Optional<TripCreatedDto> trip = tripCommandService.handle(CreateTripCommandFromResourceAssembler.toCommandFromResource(resource));
            return trip.map(TripInformationResourceFromEntityAssembler::toResourceFromDto)
                    .map(tripInformationResource -> ResponseEntity.status(HttpStatus.CREATED).body(tripInformationResource))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if(tripQueryService.handle(new CheckTripExistsByIdQuery(id))){
                tripCommandService.handle(new DeleteTripCommand(id));
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}