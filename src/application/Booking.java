

package application;

public class Booking {
    private String bookingId, flightNo, source,
                   destination, flightDate, seatNumber, seatClass, status;
    private double finalPrice;

    public Booking(String bookingId, String flightNo, String source,
                   String destination, String flightDate, String seatNumber,
                   String seatClass, double finalPrice, String status) {
        this.bookingId   = bookingId;
        this.flightNo    = flightNo;
        this.source      = source;
        this.destination = destination;
        this.flightDate  = flightDate;
        this.seatNumber  = seatNumber;
        this.seatClass   = seatClass;
        this.finalPrice  = finalPrice;
        this.status      = status;
    }

    public String getBookingId()   { return bookingId; }
    public String getFlightNo()    { return flightNo; }
    public String getSource()      { return source; }
    public String getDestination() { return destination; }
    public String getFlightDate()  { return flightDate; }
    public String getSeatNumber()  { return seatNumber; }
    public String getSeatClass()   { return seatClass; }
    public double getFinalPrice()  { return finalPrice; }
    public String getStatus()      { return status; }
}