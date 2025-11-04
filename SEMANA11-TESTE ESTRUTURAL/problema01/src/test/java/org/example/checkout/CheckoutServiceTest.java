package org.example.checkout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CheckoutService Tests")
public class CheckoutServiceTest {

    private CheckoutService service;
    private CouponService couponService;
    private ShippingService shippingService;
    private LocalDate today;

    @BeforeEach
    @DisplayName("Setup before each test")
    public void setUp() {
        couponService = new CouponService();
        shippingService = new ShippingService();
        service = new CheckoutService(couponService, shippingService);
        today = LocalDate.of(2025, 1, 1);
    }

    @Test
    @DisplayName("Should calculate total with BASIC tier and no discounts")
    public void testBasicTierNoDiscounts() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "SUL", 2.0, null, today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(0.0, result.discountValue, 0.01);
        assertEquals(12.0, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(132.0, result.total, 0.01);
    }

    @Test
    @DisplayName("Should apply SILVER tier discount")
    public void testSilverTierDiscount() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.SILVER, false, "SUL", 2.0, null, today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(5.0, result.discountValue, 0.01);
        assertEquals(11.4, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(126.4, result.total, 0.01);
    }

    @Test
    @DisplayName("Should apply GOLD tier discount")
    public void testGoldTierDiscount() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.GOLD, false, "SUL", 2.0, null, today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(10.0, result.discountValue, 0.01);
        assertEquals(10.8, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(120.8, result.total, 0.01);
    }

    @Test
    @DisplayName("Should apply first purchase discount")
    public void testFirstPurchaseDiscount() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, true, "SUL", 2.0, null, today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(5.0, result.discountValue, 0.01);
        assertEquals(11.4, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(126.4, result.total, 0.01);
    }

    @Test
    @DisplayName("Should not apply first purchase discount below minimum")
    public void testFirstPurchaseMinimumNotMet() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 40.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, true, "SUL", 2.0, null, today, null);

        assertEquals(40.0, result.subtotal, 0.01);
        assertEquals(0.0, result.discountValue, 0.01);
        assertEquals(4.8, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(64.8, result.total, 0.01);
    }

    @Test
    @DisplayName("Should apply DESC10 coupon discount")
    public void testDesc10Coupon() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "SUL", 2.0, "DESC10", today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(10.0, result.discountValue, 0.01);
        assertEquals(10.8, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(120.8, result.total, 0.01);
    }

    @Test
    @DisplayName("Should apply DESC20 coupon when valid")
    public void testDesc20CouponValid() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 150.0, 1));
        LocalDate expiry = LocalDate.of(2025, 1, 31);

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "SUL", 2.0, "DESC20", today, expiry);

        assertEquals(150.0, result.subtotal, 0.01);
        assertEquals(30.0, result.discountValue, 0.01);
        assertEquals(14.4, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(154.4, result.total, 0.01);
    }

    @Test
    @DisplayName("Should apply free shipping coupon")
    public void testFreteGratisCoupon() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "SUL", 3.0, "FRETEGRATIS", today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(0.0, result.discountValue, 0.01);
        assertEquals(12.0, result.tax, 0.01);
        assertEquals(0.0, result.shipping, 0.01);
        assertEquals(112.0, result.total, 0.01);
    }

    @Test
    @DisplayName("Should cap discount at 30%")
    public void testDiscountCap30Percent() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));
        LocalDate expiry = LocalDate.of(2025, 1, 31);

        CheckoutResult result = service.checkout(items, CustomerTier.GOLD, true, "SUL", 2.0, "DESC20", today, expiry);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(30.0, result.discountValue, 0.01);
        assertEquals(8.4, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(98.4, result.total, 0.01);
    }

    @Test
    @DisplayName("Should exempt BOOK category from tax")
    public void testBookCategoryTaxExempt() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("BOOK", 100.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "SUL", 2.0, null, today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(0.0, result.discountValue, 0.01);
        assertEquals(0.0, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(120.0, result.total, 0.01);
    }

    @Test
    @DisplayName("Should tax only non-BOOK categories in mixed purchases")
    public void testMixedCategoriesToxTaxableOnly() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 80.0, 1));
        items.add(new Item("BOOK", 20.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "SUL", 2.0, null, today, null);

        assertEquals(100.0, result.subtotal, 0.01);
        assertEquals(0.0, result.discountValue, 0.01);
        assertEquals(9.6, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(129.6, result.total, 0.01);
    }

    @Test
    @DisplayName("Should waive shipping for high subtotal")
    public void testFreeShippingBySubtotal() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 300.0, 1));

        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "NORTE", 10.0, null, today, null);

        assertEquals(300.0, result.subtotal, 0.01);
        assertEquals(0.0, result.discountValue, 0.01);
        assertEquals(36.0, result.tax, 0.01);
        assertEquals(0.0, result.shipping, 0.01);
        assertEquals(336.0, result.total, 0.01);
    }

    @Test
    @DisplayName("Should process multiple items correctly")
    public void testMultipleItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 50.0, 2));
        items.add(new Item("ROUPAS", 25.0, 4));

        CheckoutResult result = service.checkout(items, CustomerTier.SILVER, false, "SUL", 3.0, null, today, null);

        assertEquals(200.0, result.subtotal, 0.01);
        assertEquals(10.0, result.discountValue, 0.01);
        assertEquals(22.8, result.tax, 0.01);
        assertEquals(35.0, result.shipping, 0.01);
        assertEquals(247.8, result.total, 0.01);
    }

    @Test
    @DisplayName("Should throw exception for null items list")
    public void testNullItemsList() {
        assertThrows(NullPointerException.class, () ->
                service.checkout(null, CustomerTier.BASIC, false, "SUL", 2.0, null, today, null)
        );
    }

    @Test
    @DisplayName("Should throw exception for null customer tier")
    public void testNullTier() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("ELETRÔNICOS", 100.0, 1));
        assertThrows(NullPointerException.class, () ->
                service.checkout(items, null, false, "SUL", 2.0, null, today, null)
        );
    }

    @Test
    @DisplayName("Should handle empty items list")
    public void testEmptyItemsList() {
        List<Item> items = new ArrayList<>();
        CheckoutResult result = service.checkout(items, CustomerTier.BASIC, false, "SUL", 2.0, null, today, null);

        assertEquals(0.0, result.subtotal, 0.01);
        assertEquals(0.0, result.discountValue, 0.01);
        assertEquals(0.0, result.tax, 0.01);
        assertEquals(20.0, result.shipping, 0.01);
        assertEquals(20.0, result.total, 0.01);
    }
}
