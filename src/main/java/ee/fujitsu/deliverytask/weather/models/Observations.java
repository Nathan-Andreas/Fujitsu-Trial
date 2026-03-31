package ee.fujitsu.deliverytask.weather.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import java.util.List;

// Root element for weather observations XML mapping.
@Data
@JacksonXmlRootElement(localName = "observations")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Observations {

    @JacksonXmlProperty(isAttribute = true)
    private Long timestamp;

    @JacksonXmlProperty(localName = "station")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Station> stations;
}