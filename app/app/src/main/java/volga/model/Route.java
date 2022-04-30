package volga.model;


import java.util.Date;
import java.util.Objects;

public class Route {

    private Date[] estimatedArrival;
    private String name;


    public Route(Date[] estimatedArrival, String name) {
        this.estimatedArrival = estimatedArrival;
        this.name = name;
    }

    public Route() {
    }

    public Date[] getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(Date[] estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return name.equals(route.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
