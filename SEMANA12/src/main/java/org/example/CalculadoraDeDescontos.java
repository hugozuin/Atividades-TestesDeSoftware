package org.example;

public class CalculadoraDeDescontos {
    public double calcular(double valor){
        if(valor > 0 && valor < 100 ){
            return valor;
        }else if(valor >= 100 && valor <= 500){
            valor = valor - (valor * 0.05);
            return valor;
        }else if(valor > 500){
            valor = valor - (valor * 0.1);
            return valor;
        }else{
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }
    }
}
