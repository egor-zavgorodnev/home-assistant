package volga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import volga.model.ArrivalInfoResponse;

public class StationsInfoService {

    public List<ArrivalInfoResponse> getInfo(String stationJson, Set<String> vehicles) {
        List<ArrivalInfoResponse> result = new ArrayList<>();

        Arrays.stream(DataParser.getRoutesFromJson(stationJson))
                .filter(value -> vehicles.contains(value.getName()))
                .forEach(route -> {
                    if (route.getEstimatedArrival() == null) {
                        result.add(new ArrivalInfoResponse(route.getName(), Long.MAX_VALUE));
                    } else {
                        double millisToArrival = route.getEstimatedArrival()[0].getTime() - System.currentTimeMillis();
                        if (millisToArrival < 0) {
                            result.add(new ArrivalInfoResponse(route.getName(), Long.MAX_VALUE));
                        } else {
                            Double minutesToArrival = millisToArrival / 60000;

                            result.add(new ArrivalInfoResponse(route.getName(), Math.round(minutesToArrival)));
                        }

                    }
                });

        return result.stream().sorted(Comparator.comparingLong(ArrivalInfoResponse::getArrivalMinutes)).collect(Collectors.toList());
    }

}
