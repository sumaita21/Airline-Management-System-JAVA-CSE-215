package application;

public class Flight {
    private String flightNo;
    private String from;
    private String to;
    private String date;
    private String time;
    private int seats;

    public Flight(String flightNo, String from, String to,
                  String date, String time, int seats) {
        this.flightNo = flightNo;
        this.from     = from;
        this.to       = to;
        this.date     = date;
        this.time     = time;
        this.seats    = seats;
    }

    public String getFlightNo() { return flightNo; }
    public String getFrom()     { return from; }
    public String getTo()       { return to; }
    public String getDate()     { return date; }
    public String getTime()     { return time; }
    public int    getSeats()    { return seats; }
}