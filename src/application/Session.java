package application;

public class Session {
    public static int    userId;
    public static String username;
    public static String fullName;
    public static String role;

    // Flight booking session
    public static FlightSearch selectedFlight;
    public static String passengerName;
    public static String selectedSeat;
    public static String selectedClass;
    public static double originalPrice;
    public static double discountPercent;
    public static double finalPrice;

    // Payment session
    public static String bookingId;       // ADD THIS
    public static String paymentMethod;   // ADD THIS
}