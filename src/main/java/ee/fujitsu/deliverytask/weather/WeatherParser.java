package ee.fujitsu.deliverytask.weather;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ee.fujitsu.deliverytask.weather.models.Observations;
import ee.fujitsu.deliverytask.weather.models.Station;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

// Component responsible for parsing weather XML data and converting it to internal entities.

@Component
@RequiredArgsConstructor // Automatically injects XmlMapper
public class WeatherParser {

    private final XmlMapper xmlMapper;
    private static final Set<String> TARGET_STATIONS = Set.of("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");

    /**
     * Parses XML InputStream and returns a list of WeatherObservation entities.
     */
    public List<WeatherObservation> parse(InputStream xmlStream) {
        try {
            // Read directly from the stream instead of a massive String
            Observations xmlObs = xmlMapper.readValue(xmlStream, Observations.class);
            List<WeatherObservation> entities = new ArrayList<>();

            LocalDateTime observationTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(xmlObs.getTimestamp()),
                    TimeZone.getDefault().toZoneId()
            );

            for (Station station : xmlObs.getStations()) {
                if (TARGET_STATIONS.contains(station.getName())) {
                    WeatherObservation obs = new WeatherObservation();
                    obs.setStationName(station.getName());
                    obs.setWmoCode(station.getWmocode());
                    obs.setAirTemperature(parseSafeDouble(station.getAirtemperature()));
                    obs.setWindSpeed(parseSafeDouble(station.getWindspeed()));
                    obs.setPhenomenon(station.getPhenomenon());
                    obs.setTimestamp(observationTime);
                    entities.add(obs);
                }
            }
            return entities;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse weather XML stream", e);
        }
    }

    private Double parseSafeDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return Double.parseDouble(value); }
        catch (NumberFormatException e) { return null; }
    }
}