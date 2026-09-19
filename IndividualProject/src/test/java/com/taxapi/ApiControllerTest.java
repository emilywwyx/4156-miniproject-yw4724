package com.taxapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@Import(TestConfig.class)
class ApiControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private LocalStorageService localStorageService;

    private MockMvc mockMvc;

    @TempDir
    Path tempDir;

    protected static final String VALID_KEY = "valid-key";

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Files.writeString(tempDir.resolve("clients.json"),
            "[{\"id\":\"client-1\",\"name\":\"Alice\",\"apiKey\":\"valid-key\"}]");
        Files.writeString(tempDir.resolve("items.json"),
            "[{\"id\":\"item-1\",\"name\":\"Laptop\",\"category\":\"electronics\",\"basePrice\":999.99}]");
        Files.writeString(tempDir.resolve("taxrates.json"),
            "[{\"state\":\"CA\",\"category\":\"electronics\",\"rate\":0.0725},"
            + "{\"state\":\"NY\",\"category\":\"clothing\",\"rate\":0.04}]");

        localStorageService.setDirectory(tempDir);
    }

    // TODO(student): add @Test methods that exercise ApiController endpoints
    // via mockMvc.perform(...). Aim for >= 55% JaCoCo coverage overall.

    @Test
    void getItemsWithValidApiKeyReturnsOk() throws Exception {
        mockMvc.perform(
                get("/v1/items")
                    .header("X-API-Key", VALID_KEY)
            )
            .andExpect(status().isOk());
    }

    @Test
    void getItemsWithInvalidApiKeyReturnsUnauthorized() throws Exception {
        mockMvc.perform(
                get("/v1/items")
                    .header("X-API-Key", "wrong-key")
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getItemByIdReturnsOkWhenFound() throws Exception {
        mockMvc.perform(
                get("/v1/items/Laptop")
                    .header("X-API-Key", VALID_KEY)
            )
            .andExpect(status().isOk());
    }

    @Test
    void getItemByIdReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(
                get("/v1/items/does-not-exist")
                    .header("X-API-Key", VALID_KEY)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void getItemByIdReturnsUnauthorizedWithBadKey() throws Exception {
        mockMvc.perform(
                get("/v1/items/Laptop")
                    .header("X-API-Key", "wrong-key")
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteItemReturnsNoContentWhenFound() throws Exception {
        mockMvc.perform(
                delete("/v1/items/Laptop")
                    .header("X-API-Key", VALID_KEY)
            )
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteItemReturnsNotFoundWhenMissing() throws Exception {
        mockMvc.perform(
                delete("/v1/items/does-not-exist")
                    .header("X-API-Key", VALID_KEY)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteItemReturnsUnauthorizedWithBadKey() throws Exception {
        mockMvc.perform(
                delete("/v1/items/Laptop")
                    .header("X-API-Key", "wrong-key")
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getSupportedReturnsOkWithValidKey() throws Exception {
        mockMvc.perform(
                get("/v1/supported")
                    .header("X-API-Key", VALID_KEY)
            )
            .andExpect(status().isOk());
    }

    @Test
    void getSupportedReturnsUnauthorizedWithBadKey() throws Exception {
        mockMvc.perform(
                get("/v1/supported")
                    .header("X-API-Key", "wrong-key")
            )
            .andExpect(status().isUnauthorized());
    }
}
