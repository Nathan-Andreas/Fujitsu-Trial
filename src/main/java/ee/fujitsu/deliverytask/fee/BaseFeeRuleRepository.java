package ee.fujitsu.deliverytask.fee;

import ee.fujitsu.deliverytask.fee.models.BaseFeeRule;
import ee.fujitsu.deliverytask.fee.models.City;
import ee.fujitsu.deliverytask.fee.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BaseFeeRuleRepository extends JpaRepository<BaseFeeRule, Long> {
    Optional<BaseFeeRule> findByCityAndVehicle(City city, Vehicle vehicle);
}