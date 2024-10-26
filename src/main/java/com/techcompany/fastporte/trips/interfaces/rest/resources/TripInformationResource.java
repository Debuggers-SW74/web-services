package com.techcompany.fastporte.trips.interfaces.rest.resources;

public record TripInformationResource(
        Long tripId,
        Long driverId,
        String driverName,
        Long supervisorId,
        String supervisorName,
        String origin,
        String destination,
        String startTime,
        String endTime,
        String status
) {
}
