package org.example.checkout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShippingService Tests")
public class ShippingServiceTest {

    private final ShippingService service = new ShippingService();

    @Test
    @DisplayName("Should waive shipping when free shipping flag is true")
    public void testFreeShippingByFlag() {
        double result = service.calculate("SUL", 10.0, 100.0, true);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    @DisplayName("Should waive shipping when subtotal exceeds threshold")
    public void testFreeShippingBySubtotal() {
        double result = service.calculate("NORTE", 10.0, 300.0, false);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    @DisplayName("Should calculate shipping for SUL region")
    public void testSulRegion() {
        double result = service.calculate("SUL", 3.0, 100.0, false);
        assertEquals(35.0, result, 0.01);
    }

    @Test
    @DisplayName("Should calculate shipping for SUDESTE region")
    public void testSudesteRegion() {
        double result = service.calculate("SUDESTE", 5.0, 100.0, false);
        assertEquals(35.0, result, 0.01);
    }

    @Test
    @DisplayName("Should calculate shipping for NORTE region")
    public void testNorteRegion() {
        double result = service.calculate("NORTE", 5.0, 100.0, false);
        assertEquals(55.0, result, 0.01);
    }

    @Test
    @DisplayName("Should apply default shipping for unknown region")
    public void testDefaultRegion() {
        double result = service.calculate("NORDESTE", 5.0, 100.0, false);
        assertEquals(40.0, result, 0.01);
    }

    @Test
    @DisplayName("Should handle null region as default")
    public void testInvalidRegionDefault() {
        double result = service.calculate(null, 5.0, 100.0, false);
        assertEquals(40.0, result, 0.01);
    }

    @Test
    @DisplayName("Should throw exception for negative weight")
    public void testNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () ->
                service.calculate("SUL", -1.0, 100.0, false)
        );
    }
}
