package volga.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Preset {
    public String name; //unique
    public Map<String, Set<String>> stationAndBuses;

    public Preset(String name, Map<String, Set<String>> stationAndBuses) {
        this.name = name;
        this.stationAndBuses = stationAndBuses;
    }

    public String getName() {
        return name;
    }

    public Map<String, Set<String>> getStationAndBuses() {
        return stationAndBuses;
    }
}
