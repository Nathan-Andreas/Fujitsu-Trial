package ee.fujitsu.deliverytask.api;

import lombok.AllArgsConstructor;
import lombok.Data;

// Data Transfer Object representing the calculated delivery fee response

@Data
@AllArgsConstructor
public class FeeResponse {
    private Double fee;
}