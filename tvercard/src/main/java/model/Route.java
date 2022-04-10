package model;

import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class Route {

    private Date[] estimatedArrival;
    private String name;


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
