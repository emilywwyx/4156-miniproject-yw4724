package com.taxapi;

import com.taxapi.service.TaxApiService;
import com.taxapi.model.TaxQuoteRequest;
import com.taxapi.model.TaxQuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestConfig.class)
class TaxApiServiceUnitTests {

    @Autowired
    private TaxApiService service;

    @Autowired
    private LocalStorageService localStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        Files.writeString(tempDir.resolve("clients.json"),
            "[{\"id\":\"client-1\",\"name\":\"Alice\",\"apiKey\":\"valid-key\"}]");
        Files.writeString(tempDir.resolve("items.json"),
            "[{\"id\":\"item-1\",\"name\":\"Laptop\",\"category\":\"electronics\",\"basePrice\":999.99}]");
        Files.writeString(tempDir.resolve("taxrates.json"),
            "[{\"state\":\"CA\",\"category\":\"electronics\",\"rate\":0.0725},"
            + "{\"state\":\"NY\",\"category\":\"clothing\",\"rate\":0.04}]");

        localStorageService.setDirectory(tempDir);
    }

    // TODO(student): add @Test methods that exercise TaxApiService directly.
    // The `service` field above is the autowired bean under test.

    @Test
    void validateApiKeyReturnFalseWhenNull() throws Exception {
        assertFalse(service.validateApiKey(null));
    }

    @Test
    void validateApiKeyReturnsTrueWhenKnown() throws Exception {
        assertTrue(service.validateApiKey("valid-key"));
    }

    @Test
    void validateApiKeyReturnsFalseWhenUnknown() throws Exception {
        assertFalse(service.validateApiKey("wrong-key"));
    }

    @Test
    void getItemByIdReturnsItemWhenFund() throws Exception {
        var item = service.getItemById("Laptop");

        assertEquals("Laptop", item.getName());
    }

    @Test
    void getItemByIdReturnsNullWhenMissing() throws Exception {
        var item = service.getItemById("does-not-exist");

        assertNull(item);
    }

    @Test
    void deleteItemReturnsTrueWhenRemoved() throws Exception {
        boolean deleted = service.deleteItem("Laptop");

        assertTrue(deleted);
    }

    @Test
    void deleteItemReturnsFalseWhenMissing() throws Exception {
        boolean deleted = service.deleteItem("does-not-exist");

        assertFalse(deleted);
    }

    @Test
    void calculateTaxReturnsQuoteWhenRatFound() throws Exception {
        TaxQuoteRequest request = new TaxQuoteRequest();
        request.setState("CA");
        request.setCategory("electronics");
        request.setPrice(100.0);

        TaxQuoteResponse response = service.calculateTax(request);

        assertNotNull(response);
        assertEquals(0.0725, response.getTaxRate());
    }

    @Test
    void calculateTaxReturnsNullWhenRateNotFound() throws Exception {
        TaxQuoteRequest request = new TaxQuoteRequest();
        request.setState("TX");
        request.setCategory("electronics");
        request.setPrice(100.0);

        TaxQuoteResponse response = service.calculateTax(request);

        assertNull(response);
    }

    @Test
    void calculateTaxReturnsNullWhenItemIdNotFound() throws Exception {
        TaxQuoteRequest request = new TaxQuoteRequest();
        request.setState("CA");
        request.setItemId("item-1");

        TaxQuoteResponse response = service.calculateTax(request);

        assertNull(response);
    }

    @Test
    void calculateTaxReturnsQuoteWhenItemIdFound() throws Exception {
        TaxQuoteRequest request = new TaxQuoteRequest();
        request.setState("CA");
        request.setItemId("Laptop");

        TaxQuoteResponse response = service.calculateTax(request);

        assertNotNull(response);
    }
}
