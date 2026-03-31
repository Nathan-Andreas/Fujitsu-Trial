package ee.fujitsu.deliverytask;

import ee.fujitsu.deliverytask.api.DeliveryFeeController;
import ee.fujitsu.deliverytask.fee.FeeCalculationService;
import ee.fujitsu.deliverytask.fee.exception.ForbiddenVehicleException;
import ee.fujitsu.deliverytask.fee.exception.WeatherNotFoundException;
import ee.fujitsu.deliverytask.fee.models.City;
import ee.fujitsu.deliverytask.fee.models.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryFeeController.class)
class FeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeeCalculationService feeCalculationService;

    @Test
    void testGetDeliveryFee_Success_NoDateTime() throws Exception {
        // Updated to include the third parameter as null
        when(feeCalculationService.calculateTotalFee(City.TALLINN, Vehicle.CAR, null)).thenReturn(4.0);

        mockMvc.perform(get("/api/fee")
                        .param("city", "TALLINN")
                        .param("vehicle", "CAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(4.0));
    }

    @Test
    void testGetDeliveryFee_Success_WithDateTime() throws Exception {
        String timestamp = "2024-03-20T15:00:00";
        LocalDateTime parsedTime = LocalDateTime.parse(timestamp);

        // Mock the service call expecting the parsed datetime
        when(feeCalculationService.calculateTotalFee(City.TARTU, Vehicle.BIKE, parsedTime)).thenReturn(3.5);

        mockMvc.perform(get("/api/fee")
                        .param("city", "TARTU")
                        .param("vehicle", "BIKE")
                        .param("time", timestamp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(3.5));
    }

    @Test
    void testGetDeliveryFee_ForbiddenVehicle() throws Exception {
        // Updated to include the third parameter as null
        when(feeCalculationService.calculateTotalFee(City.PARNU, Vehicle.BIKE, null))
                .thenThrow(new ForbiddenVehicleException());

        mockMvc.perform(get("/api/fee")
                        .param("city", "PARNU")
                        .param("vehicle", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usage of selected vehicle type is forbidden"));
    }

    @Test
    void testGetDeliveryFee_WeatherNotFound() throws Exception {
        // Test the new exception handler
        when(feeCalculationService.calculateTotalFee(City.TALLINN, Vehicle.SCOOTER, null))
                .thenThrow(new WeatherNotFoundException("Current weather data unavailable for TALLINN"));

        mockMvc.perform(get("/api/fee")
                        .param("city", "TALLINN")
                        .param("vehicle", "SCOOTER"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Current weather data unavailable for TALLINN"));
    }
}