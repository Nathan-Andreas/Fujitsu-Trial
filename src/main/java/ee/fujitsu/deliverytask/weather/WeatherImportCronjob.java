package ee.fujitsu.deliverytask.weather;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Scheduled task for periodic weather data import.

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherImportCronjob {

    private final WeatherService weatherService;

    // Executes weather import based on the cron expression defined in application.properties.
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        log.info("Executing initial weather data fetch...");
        weatherService.importWeatherData();
    }

    //  Executes weather import based on the cron expression defined in application.properties.
    @Scheduled(cron = "${weather.import.cron:0 15 * * * *}")
    public void run() {
        log.info("Executing scheduled weather data fetch...");
        weatherService.importWeatherData();
    }
}