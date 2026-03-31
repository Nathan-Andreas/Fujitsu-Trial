package ee.fujitsu.deliverytask.api;

import ee.fujitsu.deliverytask.fee.BaseFeeRuleRepository;
import ee.fujitsu.deliverytask.fee.models.BaseFeeRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rules/base-fees")
@RequiredArgsConstructor
@Tag(name = "Business Rules API", description = "CRUD operations for managing Regional Base Fees dynamically.")
public class BusinessRuleController {

    private final BaseFeeRuleRepository repository;

    @GetMapping
    @Operation(summary = "Get all base fee rules")
    public List<BaseFeeRule> getAllRules() {
        return repository.findAll();
    }

    @PostMapping
    @Operation(summary = "Create a new base fee rule")
    public BaseFeeRule createRule(@RequestBody BaseFeeRule rule) {
        return repository.save(rule);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing base fee rule (e.g., change the price)")
    public ResponseEntity<BaseFeeRule> updateRule(@PathVariable Long id, @RequestBody BaseFeeRule updatedRule) {
        return repository.findById(id)
                .map(rule -> {
                    rule.setFee(updatedRule.getFee());
                    rule.setCity(updatedRule.getCity());
                    rule.setVehicle(updatedRule.getVehicle());
                    return ResponseEntity.ok(repository.save(rule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a base fee rule")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}