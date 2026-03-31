package ee.fujitsu.deliverytask;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ee.fujitsu.deliverytask.weather.WeatherObservation;
import ee.fujitsu.deliverytask.weather.WeatherParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherParserTest {

    // Instantiate with the required XmlMapper dependency
    private final WeatherParser weatherParser = new WeatherParser(new XmlMapper());

    @Test
    void testParse_FiltersTargetStations() {
        String mockXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <observations timestamp="1774940430">
                    <station>
                        <name>Kuressaare linn</name>
                        <wmocode></wmocode>
                        <airtemperature>0.8</airtemperature>
                        <windspeed></windspeed>
                        <phenomenon></phenomenon>
                    </station>
                    <station>
                        <name>Tallinn-Harku</name>
                        <wmocode>26038</wmocode>
                        <airtemperature>5.3</airtemperature>
                        <windspeed>1.2</windspeed>
                        <phenomenon>Clear</phenomenon>
                    </station>
                </observations>
                """;

        // Convert the String to an InputStream since our refactored parser expects a stream
        InputStream xmlStream = new ByteArrayInputStream(mockXml.getBytes(StandardCharsets.UTF_8));

        List<WeatherObservation> observations = weatherParser.parse(xmlStream);

        // Should ignore Kuressaare and only keep Tallinn-Harku
        assertEquals(1, observations.size());

        WeatherObservation tallinn = observations.get(0);
        assertEquals("Tallinn-Harku", tallinn.getStationName());
        assertEquals("26038", tallinn.getWmoCode());
        assertEquals(5.3, tallinn.getAirTemperature());
        assertEquals(1.2, tallinn.getWindSpeed());
        assertEquals("Clear", tallinn.getPhenomenon());
    }
}