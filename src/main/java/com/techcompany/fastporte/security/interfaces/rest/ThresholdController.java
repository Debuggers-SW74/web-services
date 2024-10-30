package com.techcompany.fastporte.security.interfaces.rest;

import com.techcompany.fastporte.security.domain.model.aggregates.entities.SafetyThreshold;
import com.techcompany.fastporte.security.domain.model.commands.CreateSafetyThresholdCommand;
import com.techcompany.fastporte.security.domain.model.commands.UpdateSafetyThresholdCommand;
import com.techcompany.fastporte.security.domain.model.queries.GetSafetyThresholdByTripIdQuery;
import com.techcompany.fastporte.security.domain.services.SafetyThresholdCommandService;
import com.techcompany.fastporte.security.domain.services.SafetyThresholdQueryService;
import com.techcompany.fastporte.security.interfaces.rest.resources.CreateSafetyThresholdResource;
import com.techcompany.fastporte.security.interfaces.rest.resources.SafetyThresholdInformationResource;
import com.techcompany.fastporte.security.interfaces.rest.resources.UpdateSafetyThresholdResource;
import com.techcompany.fastporte.security.interfaces.rest.transform.fromEntity.SafetyThresholdInformationResourceFromEntityAssembler;
import com.techcompany.fastporte.security.interfaces.rest.transform.fromResource.CreateSafetyThresholdCommandFromResourceAssembler;
import com.techcompany.fastporte.security.interfaces.rest.transform.fromResource.UpdateSafetyThresholdCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/thresholds")
public class ThresholdController {

    private final SafetyThresholdCommandService safetyThresholdCommandService;
    private final SafetyThresholdQueryService safetyThresholdQueryService;

    public ThresholdController(SafetyThresholdCommandService safetyThresholdCommandService, SafetyThresholdQueryService safetyThresholdQueryService) {
        this.safetyThresholdCommandService = safetyThresholdCommandService;
        this.safetyThresholdQueryService = safetyThresholdQueryService;
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<SafetyThresholdInformationResource>> getThresholdsByTripId(@PathVariable Long tripId) {
        try {
            List<SafetyThreshold> thresholds = safetyThresholdQueryService.handle(new GetSafetyThresholdByTripIdQuery(tripId));

            if (thresholds.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {

                var thresholdsResource = thresholds.stream()
                        .map(SafetyThresholdInformationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(thresholdsResource);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SafetyThresholdInformationResource> createThreshold(@RequestBody CreateSafetyThresholdResource createThresholdResource) {
        try {

            CreateSafetyThresholdCommand createThresholdCommand = CreateSafetyThresholdCommandFromResourceAssembler.toCommandFromResource(createThresholdResource);
            Optional<SafetyThreshold> threshold = safetyThresholdCommandService.handle(createThresholdCommand);
            if (threshold.isPresent()) {
                var thresholdResource = SafetyThresholdInformationResourceFromEntityAssembler.toResourceFromEntity(threshold.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(thresholdResource);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SafetyThresholdInformationResource> updateThreshold(@RequestBody UpdateSafetyThresholdResource resource) {

        try{

            UpdateSafetyThresholdCommand command = UpdateSafetyThresholdCommandFromResourceAssembler.toCommandFromResource(resource);
            Optional<SafetyThreshold> threshold = safetyThresholdCommandService.handle(command);

            if (threshold.isPresent()) {
                var thresholdResource = SafetyThresholdInformationResourceFromEntityAssembler.toResourceFromEntity(threshold.get());
                return ResponseEntity.status(HttpStatus.OK).body(thresholdResource);
            } else {
                System.out.println("Threshold not updated");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}
