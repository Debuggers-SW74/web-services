package com.techcompany.fastporte.users.interfaces.rest;

import com.techcompany.fastporte.users.domain.model.aggregates.entities.Supervisor;
import com.techcompany.fastporte.users.domain.model.commands.supervisor.DeleteSupervisorCommand;
import com.techcompany.fastporte.users.domain.model.queries.supervisor.GetAllSupervisorsQuery;
import com.techcompany.fastporte.users.domain.model.queries.supervisor.GetSupervisorByIdQuery;
import com.techcompany.fastporte.users.domain.services.supervisor.SupervisorCommandService;
import com.techcompany.fastporte.users.domain.services.supervisor.SupervisorQueryService;
import com.techcompany.fastporte.users.interfaces.rest.resources.RegisterSupervisorResource;
import com.techcompany.fastporte.users.interfaces.rest.resources.SupervisorInformationResource;
import com.techcompany.fastporte.users.interfaces.rest.transform.fromEntity.SupervisorInformationResourceFromEntityAssembler;
import com.techcompany.fastporte.users.interfaces.rest.transform.fromResource.RegisterSupervisorCommandFromResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/supervisors")
public class SupervisorController {

    private final SupervisorCommandService supervisorCommandService;
    private final SupervisorQueryService supervisorQueryService;

    public SupervisorController(SupervisorCommandService supervisorCommandService, SupervisorQueryService supervisorQueryService) {
        this.supervisorCommandService = supervisorCommandService;
        this.supervisorQueryService = supervisorQueryService;
    }

    @PreAuthorize("hasRole('ROLE_SUPERVISOR') or hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SupervisorInformationResource> findById(@PathVariable Long id) {

        try {
            Optional<Supervisor> supervisor = supervisorQueryService.handle(new GetSupervisorByIdQuery(id));

            return supervisor.map(value -> ResponseEntity.ok(SupervisorInformationResourceFromEntityAssembler.toResourceFromEntity(value)))
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ROLE_SUPERVISOR') or hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SupervisorInformationResource>> findAll() {
        try {
            List<Supervisor> supervisors = supervisorQueryService.handle(new GetAllSupervisorsQuery());

            if (supervisors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                var supervisorInformationResources = supervisors.stream()
                        .map(SupervisorInformationResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();

                return ResponseEntity.status(HttpStatus.OK).body(supervisorInformationResources);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SupervisorInformationResource> save(@RequestBody RegisterSupervisorResource resource) {

        try {
            Optional<Supervisor> supervisor = supervisorCommandService.handle(RegisterSupervisorCommandFromResourceAssembler.toCommandFromResource(resource));
            return supervisor.map(value -> ResponseEntity.status(HttpStatus.CREATED).body(SupervisorInformationResourceFromEntityAssembler.toResourceFromEntity(value)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ROLE_SUPERVISOR') or hasRole('ROLE_DRIVER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            if (supervisorQueryService.handle(new GetSupervisorByIdQuery(id)).isPresent()) {
                supervisorCommandService.handle(new DeleteSupervisorCommand(id));
                
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}