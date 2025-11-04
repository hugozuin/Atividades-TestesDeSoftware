package org.example.checkout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CouponService Tests")
public class CouponServiceTest {

    private final CouponService service = new CouponService();

    @Test
    @DisplayName("Should apply 10% discount")
    public void testDesc10Discount() {
        LocalDate today = LocalDate.of(2025, 1, 1);
        CouponResult result = service.evaluate("DESC10", today, today, 50.0);
        assertEquals(0.10, result.percent, 0.01);
        assertFalse(result.freeShipping);
    }

    @Test
    @DisplayName("Should apply 20% discount when minimum is met")
    public void testDesc20WithValidMinimum() {
        LocalDate today = LocalDate.of(2025, 1, 1);
        LocalDate expiry = LocalDate.of(2025, 1, 31);
        CouponResult result = service.evaluate("DESC20", today, expiry, 150.0);
        assertEquals(0.20, result.percent, 0.01);
    }

    @Test
    @DisplayName("Should not apply 20% discount below minimum")
    public void testDesc20BelowMinimum() {
        LocalDate today = LocalDate.of(2025, 1, 1);
        LocalDate expiry = LocalDate.of(2025, 1, 31);
        CouponResult result = service.evaluate("DESC20", today, expiry, 50.0);
        assertEquals(0.0, result.percent, 0.01);
    }

    @Test
    @DisplayName("Should reject expired coupon DESC20")
    public void testDesc20Expired() {
        LocalDate today = LocalDate.of(2025, 1, 31);
        LocalDate expiry = LocalDate.of(2025, 1, 30);
        CouponResult result = service.evaluate("DESC20", today, expiry, 150.0);
        assertEquals(0.0, result.percent, 0.01);
    }

    @Test
    @DisplayName("Should apply free shipping coupon")
    public void testFreteGratisCoupon() {
        LocalDate today = LocalDate.of(2025, 1, 1);
        CouponResult result = service.evaluate("FRETEGRATIS", today, null, 50.0);
        assertTrue(result.freeShipping);
    }

    @Test
    @DisplayName("Should return zero discount for invalid coupon")
    public void testInvalidCoupon() {
        LocalDate today = LocalDate.of(2025, 1, 1);
        CouponResult result = service.evaluate("INVALID", today, null, 50.0);
        assertEquals(0.0, result.percent, 0.01);
        assertFalse(result.freeShipping);
    }

    @Test
    @DisplayName("Should handle null coupon gracefully")
    public void testNullCoupon() {
        LocalDate today = LocalDate.of(2025, 1, 1);
        CouponResult result = service.evaluate(null, today, null, 50.0);
        assertEquals(0.0, result.percent, 0.01);
    }
}
