package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraDeDescontosTest {

    @Test
    public void compraMenorQue100Desconto0(){
        CalculadoraDeDescontos calc = new CalculadoraDeDescontos();
        double result = calc.calcular(99.99);
        assertEquals(99.99, result);
    }

    @Test
    public void compraIgual100Desconto5(){
        CalculadoraDeDescontos calc = new CalculadoraDeDescontos();
        double result = calc.calcular(100.00);
        assertEquals(95.00, result);
    }
    @Test
    public void compraIgual500Desconto5(){
        CalculadoraDeDescontos calc = new CalculadoraDeDescontos();
        double result = calc.calcular(500.00);
        assertEquals(475.00, result);
    }
    @Test
    public void compraMaiorQue500Desconto10(){
        CalculadoraDeDescontos calc = new CalculadoraDeDescontos();
        double result = calc.calcular(550.00);
        assertEquals(495.00, result);
    }
    @Test
    public void compraComValorNegativo(){
        CalculadoraDeDescontos calc = new CalculadoraDeDescontos();
        assertThrows(IllegalArgumentException.class, () -> calc.calcular(-50.00));
    }
}