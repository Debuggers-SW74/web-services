package com.techcompany.fastporte.trips.application.internal.commandservices;

import com.techcompany.fastporte.shared.dtos.DriverInformationDto;
import com.techcompany.fastporte.trips.application.dtos.TripCreatedDto;
import com.techcompany.fastporte.trips.domain.model.aggregates.entities.Trip;
import com.techcompany.fastporte.trips.domain.model.aggregates.entities.TripStatus;
import com.techcompany.fastporte.trips.domain.model.aggregates.enums.Status;
import com.techcompany.fastporte.trips.domain.model.commands.CreateTripCommand;
import com.techcompany.fastporte.trips.domain.model.commands.DeleteTripCommand;
import com.techcompany.fastporte.trips.domain.services.TripCommandService;
import com.techcompany.fastporte.trips.infrastructure.acl.DriverAcl;
import com.techcompany.fastporte.trips.infrastructure.persistence.jpa.TripRepository;
import com.techcompany.fastporte.trips.infrastructure.persistence.jpa.TripStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TripCommandServiceImp implements TripCommandService {

    private final TripRepository tripRepository;
    private final TripStatusRepository tripStatusRepository;
    private final DriverAcl driverAcl;

    public TripCommandServiceImp(TripRepository tripRepository, TripStatusRepository tripStatusRepository, DriverAcl driverAcl) {
        this.tripRepository = tripRepository;
        this.tripStatusRepository = tripStatusRepository;
        this.driverAcl = driverAcl;
    }

    @Override
    public Optional<TripCreatedDto> handle(CreateTripCommand command) {

        // Mapear el objeto TripRegisterDto a un objeto Trip
        Trip trip = new Trip(command);

        // Asignar el status del viaje
        TripStatus tripStatus = tripStatusRepository.findByStatus(Status.PENDING)
                .orElseThrow(() -> new RuntimeException("Error: El status del viaje no existe en la base de datos"));

        trip.setStatus(tripStatus);

        // Verificar si el conductor existe
        Optional<DriverInformationDto> driver = driverAcl.findDriverById(trip.getDriverId());

        if (driver.isPresent()) {
            // Persistir el viaje
            trip = tripRepository.save(trip);

            // Mapear el objeto Trip a un objeto TripCreatedDto
            TripCreatedDto tripCreatedDto = TripCreatedDto.builder()
                    .tripId(trip.getId())
                    .driverId(driver.get().id())
                    .driverName(driver.get().name() + " " + driver.get().firstLastName() + " " + driver.get().secondLastName())
                    .origin(trip.getOrigin())
                    .destination(trip.getDestination())
                    .startTime(trip.getStartTime())
                    .endTime(trip.getEndTime())
                    .status(trip.getStatus().getStatus())
                    .build();

            return Optional.of(tripCreatedDto);

        } else {
            throw new RuntimeException("Error: El conductor con el id '" + trip.getDriverId() + "' no existe en la base de datos");
        }
    }

    @Override
    public void handle(DeleteTripCommand command) {
        tripRepository.deleteById(command.tripId());
    }
}
