package ee.fujitsu.deliverytask.weather;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Entity for storing historical weather observation data.

@Entity
@Table(name = "weather_observation", indexes = {
        @Index(name = "idx_station_timestamp", columnList = "station_name, timestamp DESC")
})
@Data
public class WeatherObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "wmo_code")
    private String wmoCode;

    @Column(name = "air_temperature")
    private Double airTemperature;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "phenomenon")
    private String phenomenon;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}