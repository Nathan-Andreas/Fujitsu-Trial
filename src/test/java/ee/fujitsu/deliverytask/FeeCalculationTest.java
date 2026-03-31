package ee.fujitsu.deliverytask;

import ee.fujitsu.deliverytask.fee.BaseFeeRuleRepository;
import ee.fujitsu.deliverytask.fee.FeeCalculationService;
import ee.fujitsu.deliverytask.fee.WeatherFeeRuleRepository;
import ee.fujitsu.deliverytask.fee.exception.ForbiddenVehicleException;
import ee.fujitsu.deliverytask.fee.exception.WeatherNotFoundException;
import ee.fujitsu.deliverytask.fee.models.*;
import ee.fujitsu.deliverytask.weather.WeatherObservation;
import ee.fujitsu.deliverytask.weather.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeCalculationTest {

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private BaseFeeRuleRepository baseFeeRuleRepository;

    @Mock
    private WeatherFeeRuleRepository weatherFeeRuleRepository;

    @InjectMocks
    private FeeCalculationService feeService;

    private WeatherObservation mockWeather;

    @BeforeEach
    void setUp() {
        mockWeather = new WeatherObservation();
        mockWeather.setStationName("Tartu-Tõravere");
        lenient().when(weatherFeeRuleRepository.findByConditionTypeAndVehicle(any(), any())).thenReturn(List.of());
    }

    @Test
    void testCalculateTotalFee_PdfExample() {
        mockWeather.setAirTemperature(-2.1);
        mockWeather.setWindSpeed(4.7);
        mockWeather.setPhenomenon("Light snow shower");

        when(weatherRepository.findFirstByStationNameOrderByTimestampDesc(City.TARTU.getStationName()))
                .thenReturn(Optional.of(mockWeather));

        when(baseFeeRuleRepository.findByCityAndVehicle(City.TARTU, Vehicle.BIKE))
                .thenReturn(Optional.of(new BaseFeeRule(1L, City.TARTU, Vehicle.BIKE, 2.5)));

        when(weatherFeeRuleRepository.findByConditionTypeAndVehicle(ConditionType.TEMPERATURE, Vehicle.BIKE))
                .thenReturn(List.of(new WeatherFeeRule(1L, ConditionType.TEMPERATURE, Vehicle.BIKE, -10.0, 0.0, null, 0.5, false)));

        when(weatherFeeRuleRepository.findByConditionTypeAndVehicle(ConditionType.PHENOMENON, Vehicle.BIKE))
                .thenReturn(List.of(new WeatherFeeRule(2L, ConditionType.PHENOMENON, Vehicle.BIKE, null, null, "snow", 1.0, false)));

        Double totalFee = feeService.calculateTotalFee(City.TARTU, Vehicle.BIKE, null);

        // 2.5 (RBF) + 0.5 (ATEF) + 0.0 (WSEF) + 1.0 (WPEF) = 4.0
        assertEquals(4.0, totalFee);
    }

    @Test
    void testCalculateTotalFee_HighWind_ThrowsException() {
        mockWeather.setAirTemperature(5.0);
        mockWeather.setWindSpeed(21.0); // Greater than 20 m/s
        mockWeather.setPhenomenon("Clear");

        when(weatherRepository.findFirstByStationNameOrderByTimestampDesc(City.TARTU.getStationName()))
                .thenReturn(Optional.of(mockWeather));

        when(baseFeeRuleRepository.findByCityAndVehicle(City.TARTU, Vehicle.BIKE))
                .thenReturn(Optional.of(new BaseFeeRule(1L, City.TARTU, Vehicle.BIKE, 2.5)));

        when(weatherFeeRuleRepository.findByConditionTypeAndVehicle(ConditionType.WIND, Vehicle.BIKE))
                .thenReturn(List.of(new WeatherFeeRule(1L, ConditionType.WIND, Vehicle.BIKE, 20.0, null, null, 0.0, true)));

        ForbiddenVehicleException exception = assertThrows(ForbiddenVehicleException.class, () -> {
            feeService.calculateTotalFee(City.TARTU, Vehicle.BIKE, null);
        });

        assertEquals("Usage of selected vehicle type is forbidden", exception.getMessage());
    }

    @Test
    void testCalculateTotalFee_Glaze_ThrowsException() {
        mockWeather.setAirTemperature(0.0);
        mockWeather.setWindSpeed(5.0);
        mockWeather.setPhenomenon("Glaze");

        when(weatherRepository.findFirstByStationNameOrderByTimestampDesc(City.TARTU.getStationName()))
                .thenReturn(Optional.of(mockWeather));

        when(baseFeeRuleRepository.findByCityAndVehicle(City.TARTU, Vehicle.SCOOTER))
                .thenReturn(Optional.of(new BaseFeeRule(1L, City.TARTU, Vehicle.SCOOTER, 3.0)));

        when(weatherFeeRuleRepository.findByConditionTypeAndVehicle(ConditionType.PHENOMENON, Vehicle.SCOOTER))
                .thenReturn(List.of(new WeatherFeeRule(1L, ConditionType.PHENOMENON, Vehicle.SCOOTER, null, null, "glaze", 0.0, true)));

        ForbiddenVehicleException exception = assertThrows(ForbiddenVehicleException.class, () -> {
            feeService.calculateTotalFee(City.TARTU, Vehicle.SCOOTER, null);
        });

        assertEquals("Usage of selected vehicle type is forbidden", exception.getMessage());
    }

    @Test
    void testCalculateTotalFee_WithHistoricalTime() {
        mockWeather.setAirTemperature(-5.0); // ATEF = 0.5
        mockWeather.setWindSpeed(2.0); // WSEF = 0.0
        mockWeather.setPhenomenon("Clear"); // WPEF = 0.0

        LocalDateTime historicalTime = LocalDateTime.of(2023, 1, 1, 12, 0);

        when(weatherRepository.findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
                City.TARTU.getStationName(), historicalTime))
                .thenReturn(Optional.of(mockWeather));

        when(baseFeeRuleRepository.findByCityAndVehicle(City.TARTU, Vehicle.SCOOTER))
                .thenReturn(Optional.of(new BaseFeeRule(1L, City.TARTU, Vehicle.SCOOTER, 3.0)));

        when(weatherFeeRuleRepository.findByConditionTypeAndVehicle(ConditionType.TEMPERATURE, Vehicle.SCOOTER))
                .thenReturn(List.of(new WeatherFeeRule(1L, ConditionType.TEMPERATURE, Vehicle.SCOOTER, -10.0, 0.0, null, 0.5, false)));

        Double totalFee = feeService.calculateTotalFee(City.TARTU, Vehicle.SCOOTER, historicalTime);

        // 3.0 (RBF) + 0.5 (ATEF) = 3.5
        assertEquals(3.5, totalFee);
    }

    @Test
    void testCalculateTotalFee_WeatherNotFound_ThrowsException() {
        when(weatherRepository.findFirstByStationNameOrderByTimestampDesc(City.TARTU.getStationName()))
                .thenReturn(Optional.empty());

        WeatherNotFoundException exception = assertThrows(WeatherNotFoundException.class, () -> {
            feeService.calculateTotalFee(City.TARTU, Vehicle.BIKE, null);
        });

        assertEquals("Current weather data unavailable for TARTU", exception.getMessage());
    }
}