package org.example.checkout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Item Tests")
public class ItemTest {

    @Test
    @DisplayName("Should construct Item with valid parameters")
    public void testItemConstruction() {
        Item item = new Item("ELETRÔNICOS", 100.0, 2);
        assertEquals("ELETRÔNICOS", item.getCategoria());
        assertEquals(100.0, item.getPrecoUnitario(), 0.01);
        assertEquals(2, item.getQuantidade());
    }

    @Test
    @DisplayName("Should calculate correct subtotal")
    public void testItemSubtotal() {
        Item item = new Item("LIVROS", 25.50, 3);
        assertEquals(76.50, item.subtotal(), 0.01);
    }

    @Test
    @DisplayName("Should throw exception for negative price")
    public void testItemInvalidNegativePrice() {
        assertThrows(IllegalArgumentException.class, () ->
                new Item("ELETRÔNICOS", -10.0, 1)
        );
    }

    @Test
    @DisplayName("Should throw exception for zero quantity")
    public void testItemInvalidZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () ->
                new Item("LIVROS", 50.0, 0)
        );
    }
}
