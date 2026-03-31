package ee.fujitsu.deliverytask.fee.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weather_fee_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherFeeRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    @Enumerated(EnumType.STRING)
    private Vehicle vehicle;

    // For temperature and wind (null means infinity)
    private Double rangeMin;
    private Double rangeMax;

    // For weather phenomena (e.g., "snow", "rain")
    private String keyword;

    private Double fee;
    private boolean forbidden;
}