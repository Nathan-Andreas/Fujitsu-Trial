package ee.fujitsu.deliverytask.fee.exception;

// Exception thrown when weather conditions forbid the use of a specific vehicle

public class ForbiddenVehicleException extends RuntimeException {
    public ForbiddenVehicleException() {
        super("Usage of selected vehicle type is forbidden");
    }
}