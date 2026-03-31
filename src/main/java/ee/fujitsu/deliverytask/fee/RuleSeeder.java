package ee.fujitsu.deliverytask.fee;

import ee.fujitsu.deliverytask.fee.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RuleSeeder implements CommandLineRunner {

    private final BaseFeeRuleRepository baseFeeRuleRepository;
    private final WeatherFeeRuleRepository weatherFeeRuleRepository;

    @Override
    public void run(String... args) {

        // Block 1: Seed Base Fees
        if (baseFeeRuleRepository.count() == 0) {
            log.info("Seeding initial Regional Base Fee (RBF) rules into the database...");
            baseFeeRuleRepository.saveAll(List.of(
                    // Tallinn Rules
                    new BaseFeeRule(null, City.TALLINN, Vehicle.CAR, 4.0),
                    new BaseFeeRule(null, City.TALLINN, Vehicle.SCOOTER, 3.5),
                    new BaseFeeRule(null, City.TALLINN, Vehicle.BIKE, 3.0),
                    // Tartu Rules
                    new BaseFeeRule(null, City.TARTU, Vehicle.CAR, 3.5),
                    new BaseFeeRule(null, City.TARTU, Vehicle.SCOOTER, 3.0),
                    new BaseFeeRule(null, City.TARTU, Vehicle.BIKE, 2.5),
                    // Pärnu Rules
                    new BaseFeeRule(null, City.PARNU, Vehicle.CAR, 3.0),
                    new BaseFeeRule(null, City.PARNU, Vehicle.SCOOTER, 2.5),
                    new BaseFeeRule(null, City.PARNU, Vehicle.BIKE, 2.0)
            ));
            log.info("Base fee rules seeded successfully.");
        }

        // Block 2: Seed Weather Extra Fees (Un-nested so it runs independently)
        if (weatherFeeRuleRepository.count() == 0) {
            log.info("Seeding Weather Extra Fee rules...");
            weatherFeeRuleRepository.saveAll(List.of(
                    // Air Temperature Rules (Scooter & Bike)
                    new WeatherFeeRule(null, ConditionType.TEMPERATURE, Vehicle.SCOOTER, null, -10.0, null, 1.0, false),
                    new WeatherFeeRule(null, ConditionType.TEMPERATURE, Vehicle.BIKE, null, -10.0, null, 1.0, false),
                    new WeatherFeeRule(null, ConditionType.TEMPERATURE, Vehicle.SCOOTER, -10.0, 0.0, null, 0.5, false),
                    new WeatherFeeRule(null, ConditionType.TEMPERATURE, Vehicle.BIKE, -10.0, 0.0, null, 0.5, false),

                    // Wind Speed Rules (Bike Only)
                    new WeatherFeeRule(null, ConditionType.WIND, Vehicle.BIKE, 10.0, 20.0, null, 0.5, false),
                    new WeatherFeeRule(null, ConditionType.WIND, Vehicle.BIKE, 20.0, null, null, 0.0, true),

                    // Phenomenon Rules (Scooter & Bike)
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.SCOOTER, null, null, "snow", 1.0, false),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.BIKE, null, null, "snow", 1.0, false),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.SCOOTER, null, null, "sleet", 1.0, false),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.BIKE, null, null, "sleet", 1.0, false),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.SCOOTER, null, null, "rain", 0.5, false),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.BIKE, null, null, "rain", 0.5, false),

                    // Forbidden Phenomena
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.SCOOTER, null, null, "glaze", 0.0, true),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.BIKE, null, null, "glaze", 0.0, true),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.SCOOTER, null, null, "hail", 0.0, true),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.BIKE, null, null, "hail", 0.0, true),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.SCOOTER, null, null, "thunder", 0.0, true),
                    new WeatherFeeRule(null, ConditionType.PHENOMENON, Vehicle.BIKE, null, null, "thunder", 0.0, true)
            ));
            log.info("Weather extra fee rules seeded successfully.");
        }
    }
}