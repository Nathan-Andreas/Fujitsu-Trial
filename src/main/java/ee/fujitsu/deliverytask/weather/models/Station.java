package ee.fujitsu.deliverytask.weather.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

 // Represents a single station entry in the weather XML.
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {
    private String name;
    private String wmocode;
    private String airtemperature;
    private String windspeed;
    private String phenomenon;
}