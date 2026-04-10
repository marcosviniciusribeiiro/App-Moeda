package com.exemplo.ewm.appmoeda;

import com.google.gson.annotations.SerializedName;

public class Cotacao {
    @SerializedName("bid")
    private final String valor;

    public Cotacao(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

}
