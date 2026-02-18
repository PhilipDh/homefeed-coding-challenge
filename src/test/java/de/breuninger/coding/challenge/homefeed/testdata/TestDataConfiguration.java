package de.breuninger.coding.challenge.homefeed.testdata;

import de.breuninger.coding.challenge.homefeed.repository.BannerRepository;
import de.breuninger.coding.challenge.homefeed.repository.HighlightRepository;
import de.breuninger.coding.challenge.homefeed.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class TestDataConfiguration {


    @Bean
    public TestDataInitializer testDataInitializer(
            UserRepository userRepository,
            BannerRepository bannerRepository,
            HighlightRepository highlightRepository) {
        TestDataInitializer initializer = new TestDataInitializer(
                userRepository,
                bannerRepository,
                highlightRepository
        );

        initializer.initializeAllTestData();
        return initializer;
    }
}

