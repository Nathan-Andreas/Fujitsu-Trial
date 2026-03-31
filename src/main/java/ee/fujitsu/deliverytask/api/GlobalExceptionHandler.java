package ee.fujitsu.deliverytask.api;

import ee.fujitsu.deliverytask.fee.exception.ForbiddenVehicleException;
import ee.fujitsu.deliverytask.fee.exception.WeatherNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenVehicleException.class)
    public ResponseEntity<String> handleForbiddenVehicle(ForbiddenVehicleException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // New handler for WeatherNotFoundException
    @ExceptionHandler(WeatherNotFoundException.class)
    public ResponseEntity<String> handleWeatherNotFound(WeatherNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}