package de.breuninger.coding.challenge.homefeed.service;

import de.breuninger.coding.challenge.homefeed.config.HomefeedModuleConfigurationProperties;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedEntry;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModule;
import de.breuninger.coding.challenge.homefeed.service.module.HomefeedModuleGroup;
import de.breuninger.coding.challenge.homefeed.service.module.GreetingEntry;
import de.breuninger.coding.challenge.homefeed.testdata.TestDataConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestDataConfiguration.class)
class HomefeedServiceTest {

    @Autowired
    private HomefeedService homefeedService;

    @Nested
    class GetHomefeedTests {

        @Nested
        class BasicTests {

            @Test
            void shouldReturnHomefeedForUser() {
                // When
                List<HomefeedModuleGroup> result = homefeedService.getHomefeed("user123");

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(3);

                assertThat(result)
                        .extracting(HomefeedModuleGroup::type)
                        .containsExactly("banner", "greeting", "highlight");

                // Verify each module has entries
                result.forEach(module -> {
                    assertThat(module.entries())
                            .as("Module %s should have entries", module.type())
                            .isNotEmpty();
                    assertThat(module.id()).isNotNull();
                });

                // Verify the greeting contains the user's name (indicates authenticated user)
                HomefeedModuleGroup greetingModule = result.stream()
                        .filter(module -> "greeting".equals(module.type()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Greeting module not found"));

                assertThat(greetingModule.entries())
                        .isNotEmpty()
                        .first()
                        .extracting(entry -> ((GreetingEntry) entry).greeting())
                        .asString()
                        .contains("Anna", "Schmidt");
            }

            @ParameterizedTest(name = "should treat user ID ''{0}'' as anonymous")
            @NullSource
            @ValueSource(strings = {"", "   ", "unknown-user-999"})
            void shouldTreatVariousUserIdsAsAnonymous(String userId) {
                // When
                List<HomefeedModuleGroup> result = homefeedService.getHomefeed(userId);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).isNotEmpty();

                assertThat(result)
                        .extracting(HomefeedModuleGroup::type)
                        .contains("banner", "greeting", "highlight");

                HomefeedModuleGroup greetingModule = result.stream()
                        .filter(module -> "greeting".equals(module.type()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Greeting module not found"));

                assertThat(greetingModule.entries())
                        .isNotEmpty()
                        .first()
                        .extracting(entry -> ((GreetingEntry) entry).greeting())
                        .asString()
                        .contains("Welcome!");
            }
        }

        @Nested
        class TimeoutAndResilienceTests {
            @TestConfiguration
            static class SlowModuleTestConfig {

                @Bean
                @Primary
                public HomefeedModule slowTestModule(HomefeedModuleConfigurationProperties configProperties) {
                    return new HomefeedModule() {
                        @Override
                        public List<HomefeedEntry> getEntries(UserContext context) {
                            try {
                                // Sleep for longer than the configured timeout
                                // Default timeout is 250ms, we sleep for 500ms
                                long timeoutMs = configProperties.getExecutionTimeoutMs();
                                Thread.sleep(timeoutMs + 250);
                                return List.of(new GreetingEntry("This should never appear"));
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return List.of();
                            }
                        }

                        @Override
                        public String getType() {
                            return "slow-test-module";
                        }

                        @Override
                        public int getPriority() {
                            return 5;
                        }

                        @Override
                        public ModuleDisplayTypeEnum getDisplayType() {
                            return ModuleDisplayTypeEnum.SINGLE;
                        }
                    };
                }
            }

            @Test
            void shouldExcludeModuleThatExceedsTimeout() {
                long startTime = System.currentTimeMillis();

                // When
                List<HomefeedModuleGroup> result = homefeedService.getHomefeed("user123");

                long elapsedTime = System.currentTimeMillis() - startTime;

                // Then
                assertThat(result).isNotNull();
                assertThat(result)
                        .extracting(HomefeedModuleGroup::type)
                        .doesNotContain("slow-test-module");

                assertThat(result)
                        .extracting(HomefeedModuleGroup::type)
                        .contains("banner", "greeting", "highlight");

                assertThat(elapsedTime)
                        .as("Request should complete near timeout, not wait for slow module")
                        .isLessThan(1000L);

                result.forEach(module ->
                    assertThat(module.entries())
                            .as("Module %s should have entries", module.type())
                            .isNotEmpty()
                );
            }

            @Test
            void shouldHandleAnonymousUserWithSlowModule() {
                // Given
                long startTime = System.currentTimeMillis();

                // When
                List<HomefeedModuleGroup> result = homefeedService.getHomefeed(null);

                long elapsedTime = System.currentTimeMillis() - startTime;

                // Then
                assertThat(result).isNotNull();
                assertThat(result)
                        .extracting(HomefeedModuleGroup::type)
                        .doesNotContain("slow-test-module");

                assertThat(result)
                        .extracting(HomefeedModuleGroup::type)
                        .contains("banner", "greeting", "highlight");

                assertThat(elapsedTime).isLessThan(1000L);
            }
        }
    }
}



