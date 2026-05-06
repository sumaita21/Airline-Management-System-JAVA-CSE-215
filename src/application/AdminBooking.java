package application;

public class AdminBooking {
    private String bookingId, username, flightNo, source, destination, status;
    private double finalPrice;

    public AdminBooking(String bookingId, String username, String flightNo,
                        String source, String destination,
                        double finalPrice, String status) {
        this.bookingId   = bookingId;
        this.username    = username;
        this.flightNo    = flightNo;
        this.source      = source;
        this.destination = destination;
        this.finalPrice  = finalPrice;
        this.status      = status;
    }

    public String getBookingId()   { return bookingId; }
    public String getUsername()    { return username; }
    public String getFlightNo()    { return flightNo; }
    public String getSource()      { return source; }
    public String getDestination() { return destination; }
    public double getFinalPrice()  { return finalPrice; }
    public String getStatus()      { return status; }
}