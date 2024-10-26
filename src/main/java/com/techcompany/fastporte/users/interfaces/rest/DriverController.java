package com.techcompany.fastporte.users.interfaces.rest;

import com.techcompany.fastporte.users.domain.model.aggregates.entities.Driver;
import com.techcompany.fastporte.users.domain.model.aggregates.entities.UserDetailsImp;
import com.techcompany.fastporte.users.domain.model.commands.driver.DeleteDriverCommand;
import com.techcompany.fastporte.users.domain.model.queries.driver.GetAllDriversByIdInList;
import com.techcompany.fastporte.users.domain.model.queries.driver.GetAllDriversQuery;
import com.techcompany.fastporte.users.domain.model.queries.driver.GetDriverByIdQuery;
import com.techcompany.fastporte.users.domain.services.driver.DriverCommandService;
import com.techcompany.fastporte.users.domain.services.driver.DriverQueryService;
import com.techcompany.fastporte.users.interfaces.rest.resources.DriverInformationResource;
import com.techcompany.fastporte.users.interfaces.rest.resources.RegisterDriverResource;
import com.techcompany.fastporte.users.interfaces.rest.transform.fromEntity.DriverInformationResourceFromEntityAssembler;
import com.techcompany.fastporte.users.interfaces.rest.transform.fromResource.RegisterDriverCommandFromResourceAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverCommandService driverCommandService;
    private final DriverQueryService driverQueryService;

    public DriverController(DriverCommandService driverCommandService, DriverQueryService driverQueryService) {
        this.driverCommandService = driverCommandService;
        this.driverQueryService = driverQueryService;
    }

    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_SUPERVISOR') or hasRole('ROLE_ADMIN') ")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImp userDetails) {

        try {

            Optional<Driver> driver = driverQueryService.handle(new GetDriverByIdQuery(id));

            if (driver.isPresent()) {
                return ResponseEntity.ok(DriverInformationResourceFromEntityAssembler.toPublicResourceFromEntity(driver.get()));
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_SUPERVISOR') or hasRole('ROLE_ADMIN') ")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DriverInformationResource>> findAll() {
        try{
            List<Driver> drivers = driverQueryService.handle(new GetAllDriversQuery());

            if (drivers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {

                var driverInformationResources = drivers.stream()
                        .map(DriverInformationResourceFromEntityAssembler::toPublicResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(driverInformationResources);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<List<DriverInformationResource>> findAllByIdIn(@RequestBody List<Long> driverIds) {
        List<Driver> drivers = driverQueryService.handle(new GetAllDriversByIdInList(driverIds));

        var driverInformationResources = drivers.stream()
                .map(DriverInformationResourceFromEntityAssembler::toPublicResourceFromEntity)
                .toList();

        return ResponseEntity.ok(driverInformationResources);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverInformationResource> save(@Valid @RequestBody RegisterDriverResource resource) {

        try {
            Optional<Driver> driver = driverCommandService.handle(RegisterDriverCommandFromResourceAssembler.toCommandFromResource(resource));
            return driver.map(DriverInformationResourceFromEntityAssembler::toPublicResourceFromEntity)
                    .map(driverInformationResource -> ResponseEntity.status(HttpStatus.CREATED).body(driverInformationResource))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if (driverQueryService.handle(new GetDriverByIdQuery(id)).isPresent()) {
                driverCommandService.handle(new DeleteDriverCommand(id));

                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
