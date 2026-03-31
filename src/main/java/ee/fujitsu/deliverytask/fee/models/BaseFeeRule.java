package ee.fujitsu.deliverytask.fee.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "base_fee_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private Double fee;
}