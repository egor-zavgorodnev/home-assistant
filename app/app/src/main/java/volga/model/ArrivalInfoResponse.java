package volga.model;


public class ArrivalInfoResponse {
    private String busNumber;
    private long arrivalMinutes;

    public ArrivalInfoResponse(String busNumber, long arrivalMinutes) {
        this.busNumber = busNumber;
        this.arrivalMinutes = arrivalMinutes;
    }

    public ArrivalInfoResponse() {
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public long getArrivalMinutes() {
        return arrivalMinutes;
    }

    public void setArrivalMinutes(long arrivalMinutes) {
        this.arrivalMinutes = arrivalMinutes;
    }
}
