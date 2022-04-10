import exceptions.NetException;
import service.StationsInfoService;

import java.util.Map;
import java.util.Set;

public class Application {
    public static void main(String[] args) throws NetException {
        StationsInfoService service = new StationsInfoService();

        service.getInfo(Map.of("8182", Set.of("208", "56"), "8183", Set.of("7")));
    }
}
