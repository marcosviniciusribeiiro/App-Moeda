package com.exemplo.ewm.appmoeda;

import com.google.gson.annotations.SerializedName;

public class Cotacao {
    @SerializedName("bid")
    private String valor;

    public String getValor() {
        return valor;
    }
}
