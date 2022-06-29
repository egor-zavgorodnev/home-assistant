package home.assistant.model;

public class BusInfo {
    private String busNumber;
    private String arrivalTime;

    public BusInfo(String busNumber, String arrivalTime) {
        this.busNumber = busNumber;
        this.arrivalTime = arrivalTime;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
