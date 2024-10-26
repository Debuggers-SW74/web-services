package com.techcompany.fastporte.trips.application.dtos;

import com.techcompany.fastporte.trips.domain.model.aggregates.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TripInformationDto {
    private Long tripId;
    private Long driverId;
    private String driverName;
    private Long supervisorId;
    private String supervisorName;
    private String origin;
    private String destination;
    private String startTime;
    private String endTime;
    private Status status;

    public TripInformationDto() {
    }
}
