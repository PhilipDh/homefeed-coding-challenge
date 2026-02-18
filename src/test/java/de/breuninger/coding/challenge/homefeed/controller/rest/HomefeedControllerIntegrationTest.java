package de.breuninger.coding.challenge.homefeed.controller.rest;

import de.breuninger.coding.challenge.homefeed.testdata.TestDataConfiguration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestDataConfiguration.class)
class HomefeedControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class GetHomefeedEndpointTests {

        @Nested
        class HappyPathTests {

            @Test
            void shouldReturnHomefeedForKnownUser() throws Exception {
                mockMvc.perform(get("/api/v1/homefeed")
                                .header("X-User-Id", "user123"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"))
                        .andExpect(jsonPath("$.modules").isArray())
                        .andExpect(jsonPath("$.modules").isNotEmpty())
                        .andExpect(jsonPath("$.modules[?(@.type == 'greeting')]").exists())
                        .andExpect(jsonPath("$.modules[?(@.type == 'greeting')].items[0].text")
                                .value(hasItem(containsString("Anna"))))
                        .andExpect(jsonPath("$.modules[*].type").exists())
                        .andExpect(jsonPath("$.modules[*].displayType").exists())
                        .andExpect(jsonPath("$.modules[*].items").exists())
                        .andExpect(jsonPath("$.modules[*].id").exists());
            }

            @Test
            void shouldReturnHomefeedForAnonymousUser() throws Exception {
                mockMvc.perform(get("/api/v1/homefeed"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"))
                        .andExpect(jsonPath("$.modules").isArray())
                        .andExpect(jsonPath("$.modules").isNotEmpty())
                        .andExpect(jsonPath("$.modules[?(@.type == 'greeting')]").exists())
                        .andExpect(jsonPath("$.modules[?(@.type == 'greeting')].items[0].text")
                                .value(hasItem(containsString("Welcome!"))))
                        .andExpect(jsonPath("$.modules[*].type").exists())
                        .andExpect(jsonPath("$.modules[*].displayType").exists())
                        .andExpect(jsonPath("$.modules[*].items").exists())
                        .andExpect(jsonPath("$.modules[*].id").exists());
            }

            @Test
            void shouldReturnJsonContentType() throws Exception {
                mockMvc.perform(get("/api/v1/homefeed"))
                        .andExpect(status().isOk())
                        .andExpect(header().string("Content-Type", containsString("application/json")));
            }
        }


        @Nested
        class EdgeCaseTests {

            @ParameterizedTest
            @ValueSource(strings = {"unknown-user-999", ""})
            void shouldTreatUnknownAndEmptyUserAsAnonymous(String userId) throws Exception {
                mockMvc.perform(get("/api/v1/homefeed")
                                .header("X-User-Id", userId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"))
                        .andExpect(jsonPath("$.modules").isArray())
                        .andExpect(jsonPath("$.modules").isNotEmpty())
                        .andExpect(jsonPath("$.modules[?(@.type == 'greeting')]").exists())
                        .andExpect(jsonPath("$.modules[?(@.type == 'greeting')].items[0].text")
                                .value(hasItem(containsString("Welcome!"))));
            }
        }
    }
}

