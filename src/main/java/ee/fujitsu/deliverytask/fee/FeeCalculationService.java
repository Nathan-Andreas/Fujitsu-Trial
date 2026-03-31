package ee.fujitsu.deliverytask.fee;

import ee.fujitsu.deliverytask.fee.exception.ForbiddenVehicleException;
import ee.fujitsu.deliverytask.fee.exception.WeatherNotFoundException;
import ee.fujitsu.deliverytask.fee.models.*;
import ee.fujitsu.deliverytask.weather.WeatherObservation;
import ee.fujitsu.deliverytask.weather.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for calculating the total delivery fee for food couriers.
 * It evaluates Regional Base Fees (RBF) and extra weather-related fees based on dynamic
 * rules stored in the database.
 */
@Service
@RequiredArgsConstructor
public class FeeCalculationService {

    private final WeatherRepository weatherRepository;
    private final BaseFeeRuleRepository baseFeeRuleRepository;
    private final WeatherFeeRuleRepository weatherFeeRuleRepository;

    /**
     * Calculates the total delivery fee consisting of a regional base fee and extra fees for weather conditions.
     * * @param city The target city for the delivery.
     * @param vehicle The type of vehicle being used.
     * @param requestedTime Optional datetime to calculate fees based on historical weather.
     * @return The total delivery fee in Euros.
     * @throws WeatherNotFoundException if no weather data is found for the given time/city.
     */
    public Double calculateTotalFee(City city, Vehicle vehicle, LocalDateTime requestedTime) {
        WeatherObservation weather;

        // Fetch weather data: either the absolute latest  or the latest prior to the requested historical time.
        if (requestedTime == null) {
            weather = weatherRepository.findFirstByStationNameOrderByTimestampDesc(city.getStationName())
                    .orElseThrow(() -> new WeatherNotFoundException("Current weather data unavailable for " + city));
        } else {
            weather = weatherRepository.findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(city.getStationName(), requestedTime)
                    .orElseThrow(() -> new WeatherNotFoundException("Weather data unavailable for " + city + " at " + requestedTime));
        }

        // Calculate individual fee components
        double rbf = calculateRBF(city, vehicle);
        double atef = calculateATEF(weather.getAirTemperature(), vehicle);
        double wsef = calculateWSEF(weather.getWindSpeed(), vehicle);
        double wpef = calculateWPEF(weather.getPhenomenon(), vehicle);

        // Total fee is the sum of base fee and all applicable extra fees
        return rbf + atef + wsef + wpef;
    }

    /**
     * Fetches the Regional Base Fee (RBF) dynamically from the database based on city and vehicle.
     */
    private double calculateRBF(City city, Vehicle vehicle) {
        return baseFeeRuleRepository.findByCityAndVehicle(city, vehicle)
                .map(BaseFeeRule::getFee)
                .orElseThrow(() -> new RuntimeException(
                        "Configuration Error: No base fee rule defined in database for " + city + " and " + vehicle));
    }

    /**
     * Calculates the Extra Fee based on Air Temperature (ATEF).
     * Cars are exempt from this fee.
     */
    private double calculateATEF(double temp, Vehicle vehicle) {
        if (vehicle == Vehicle.CAR) return 0.0;
        return evaluateRangeRules(ConditionType.TEMPERATURE, vehicle, temp);
    }

    /**
     * Calculates the Extra Fee based on Wind Speed (WSEF).
     * This fee is strictly applicable only to bikes.
     */
    private double calculateWSEF(double wind, Vehicle vehicle) {
        if (vehicle != Vehicle.BIKE) return 0.0;
        return evaluateRangeRules(ConditionType.WIND, vehicle, wind);
    }

    /**
     * Calculates the Extra Fee based on Weather Phenomenon (WPEF).
     * Cars are exempt from this fee, and no fee is applied if there is no specific phenomenon recorded.
     */
    private double calculateWPEF(String phenomenon, Vehicle vehicle) {
        if (vehicle == Vehicle.CAR || phenomenon == null) return 0.0;

        List<WeatherFeeRule> rules = weatherFeeRuleRepository.findByConditionTypeAndVehicle(ConditionType.PHENOMENON, vehicle);
        String p = phenomenon.toLowerCase();

        // Check if the current weather phenomenon matches any keyword-based rules in the database
        for (WeatherFeeRule rule : rules) {
            if (p.contains(rule.getKeyword())) {
                if (rule.isForbidden()) throw new ForbiddenVehicleException(); // e.g., Glaze, Hail, Thunder
                return rule.getFee();
            }
        }
        return 0.0; // Extra fees are paid only for conditions listed in the rules
    }

    /**
     * Helper method to evaluate dynamic, range-based rules (Min/Max values) for temperature and wind speed.
     * @return The applicable fee, or 0.0 if no rules are matched. Throws exception if rule forbids the vehicle.
     */
    private double evaluateRangeRules(ConditionType type, Vehicle vehicle, double value) {
        List<WeatherFeeRule> rules = weatherFeeRuleRepository.findByConditionTypeAndVehicle(type, vehicle);

        for (WeatherFeeRule rule : rules) {
            // Null in the database implies "infinity" (no bound) for that direction
            boolean matchesMin = (rule.getRangeMin() == null || value >= rule.getRangeMin());
            boolean matchesMax = (rule.getRangeMax() == null || value <= rule.getRangeMax());

            if (matchesMin && matchesMax) {
                if (rule.isForbidden()) throw new ForbiddenVehicleException();
                return rule.getFee();
            }
        }
        return 0.0;
    }
}