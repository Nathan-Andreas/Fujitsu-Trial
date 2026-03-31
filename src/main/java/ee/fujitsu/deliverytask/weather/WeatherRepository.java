package ee.fujitsu.deliverytask.weather;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherObservation, Long> {

    // Retrieves the absolute latest weather data
    Optional<WeatherObservation> findFirstByStationNameOrderByTimestampDesc(String stationName);

    // Retrieves the latest weather data that was valid at or before the requested datetime
    Optional<WeatherObservation> findFirstByStationNameAndTimestampLessThanEqualOrderByTimestampDesc(
            String stationName, LocalDateTime timestamp);
}