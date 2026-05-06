package application;

public class FlightSearch {
    private int id, availableSeats;
    private String flightNo, source, destination,
                   flightDate, flightTime, status;
    private double economyPrice, businessPrice, firstPrice;

    public FlightSearch(int id, String flightNo, String source,
                        String destination, String flightDate,
                        String flightTime, double economyPrice,
                        double businessPrice, double firstPrice,
                        int availableSeats) {
        this.id             = id;
        this.flightNo       = flightNo;
        this.source         = source;
        this.destination    = destination;
        this.flightDate     = flightDate;
        this.flightTime     = flightTime;
        this.economyPrice   = economyPrice;
        this.businessPrice  = businessPrice;
        this.firstPrice     = firstPrice;
        this.availableSeats = availableSeats;
        this.status         = "scheduled";
    }

    // Constructor with status
    public FlightSearch(int id, String flightNo, String source,
                        String destination, String flightDate,
                        String flightTime, double economyPrice,
                        double businessPrice, double firstPrice,
                        int availableSeats, String status) {
        this(id, flightNo, source, destination, flightDate,
             flightTime, economyPrice, businessPrice,
             firstPrice, availableSeats);
        this.status = status;
    }

    public int    getId()             { return id; }
    public String getFlightNo()       { return flightNo; }
    public String getSource()         { return source; }
    public String getDestination()    { return destination; }
    public String getFlightDate()     { return flightDate; }
    public String getFlightTime()     { return flightTime; }
    public double getEconomyPrice()   { return economyPrice; }
    public double getBusinessPrice()  { return businessPrice; }
    public double getFirstPrice()     { return firstPrice; }
    public int    getAvailableSeats() { return availableSeats; }
    public String getStatus()         { return status; }
}