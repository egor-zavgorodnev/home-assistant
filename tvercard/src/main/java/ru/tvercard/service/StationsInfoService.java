package ru.tvercard.service;

import ru.tvercard.exceptions.NetException;
import ru.tvercard.model.ArrivalInfoResponse;
import ru.tvercard.repository.DataReceiver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class StationsInfoService {

    public List<ArrivalInfoResponse> getInfo(Map<String, Set<String>> station) throws NetException {
        DataReceiver dataReceiver = new DataReceiver();
        List<ArrivalInfoResponse> result = new ArrayList<>();

        for (String currentStation : station.keySet()) {
            String json;
            try {
                json = dataReceiver.getRoutes(currentStation);
            } catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new NetException("Problem with data retrieving");
            }

            Arrays.stream(DataParser.getRoutesFromJson(json))
                    .filter(value -> station.get(currentStation).contains(value.getName()))
                    .forEach(route -> {
                        if (route.getEstimatedArrival() == null) {
                            result.add(ArrivalInfoResponse.builder()
                                    .busNumber(route.getName())
                                    .arrivalMinutes(Long.MAX_VALUE)
                                    .build());
                        } else {
                            double millisToArrival = route.getEstimatedArrival()[0].getTime() - System.currentTimeMillis();
                            if (millisToArrival < 0) {
                                result.add(ArrivalInfoResponse.builder()
                                        .busNumber(route.getName())
                                        .arrivalMinutes(Long.MAX_VALUE)
                                        .build());
                            } else {
                                Double minutesToArrival = millisToArrival / 60000;

                                result.add(ArrivalInfoResponse.builder()
                                        .busNumber(route.getName())
                                        .arrivalMinutes(Math.round(minutesToArrival))
                                        .build());
                            }

                        }
                    });
        }

        return result.stream().sorted(Comparator.comparingLong(ArrivalInfoResponse::getArrivalMinutes)).collect(Collectors.toList());
    }

}
