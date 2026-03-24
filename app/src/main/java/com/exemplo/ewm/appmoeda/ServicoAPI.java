package com.exemplo.ewm.appmoeda;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServicoAPI {
    @GET("json/last/USD-BRL")
    Call<Map<String, Cotacao>> buscarValorCotacao();
}
