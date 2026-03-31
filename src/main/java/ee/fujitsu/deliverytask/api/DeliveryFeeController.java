package ee.fujitsu.deliverytask.api;

import ee.fujitsu.deliverytask.fee.FeeCalculationService;
import ee.fujitsu.deliverytask.fee.models.City;
import ee.fujitsu.deliverytask.fee.models.Vehicle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Delivery Fee API", description = "Endpoints for calculating food delivery fees based on weather conditions.")
public class DeliveryFeeController {

    private final FeeCalculationService feeCalculationService;

    @GetMapping("/fee")
    @Operation(summary = "Calculate Delivery Fee", description = "Calculates the total delivery fee based on city, vehicle type, and an optional historical date/time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated the delivery fee"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters or usage of selected vehicle type is forbidden"),
            @ApiResponse(responseCode = "404", description = "Weather data not found for the specified parameters")
    })
    public FeeResponse getDeliveryFee(
            @Parameter(description = "City of delivery", required = true)
            @RequestParam City city,

            @Parameter(description = "Type of vehicle used for delivery", required = true)
            @RequestParam Vehicle vehicle,

            @Parameter(description = "Optional: Calculate fee based on weather at this specific past date/time (Format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

        Double fee = feeCalculationService.calculateTotalFee(city, vehicle, time);
        return new FeeResponse(fee);
    }
}