package ee.fujitsu.deliverytask.fee;

import ee.fujitsu.deliverytask.fee.models.ConditionType;
import ee.fujitsu.deliverytask.fee.models.Vehicle;
import ee.fujitsu.deliverytask.fee.models.WeatherFeeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeatherFeeRuleRepository extends JpaRepository<WeatherFeeRule, Long> {
    List<WeatherFeeRule> findByConditionTypeAndVehicle(ConditionType type, Vehicle vehicle);
}