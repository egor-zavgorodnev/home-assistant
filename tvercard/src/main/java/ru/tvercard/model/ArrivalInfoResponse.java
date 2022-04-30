package ru.tvercard.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArrivalInfoResponse {
    private String busNumber;
    private long arrivalMinutes;
}
