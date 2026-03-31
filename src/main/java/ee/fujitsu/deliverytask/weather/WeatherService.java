package ee.fujitsu.deliverytask.weather;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import org.springframework.core.io.Resource;
import java.io.InputStream;

@Service
@RequiredArgsConstructor // Automatically injects RestTemplate and WeatherParser
@Slf4j
public class WeatherService {

    private final WeatherRepository repository;
    private final WeatherParser parser;
    private final RestTemplate restTemplate;
    private static final String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    @Transactional
    public void importWeatherData() {
        try {
            log.info("Starting weather data import...");
            // Fetch as a Resource to handle it as a stream
            Resource resource = restTemplate.getForObject(URL, Resource.class);

            if (resource != null) {
                try (InputStream inputStream = resource.getInputStream()) {
                    List<WeatherObservation> observations = parser.parse(inputStream);
                    repository.saveAll(observations);
                    log.info("Successfully imported {} weather observations.", observations.size());
                }
            }
        } catch (Exception e) {
            log.error("Error during weather data import: {}", e.getMessage());
        }
    }
}