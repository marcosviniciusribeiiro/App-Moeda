package com.exemplo.ewm.appmoeda;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServicoAPI {
    @GET("json/last/{moeda}")
    Call<Map<String, Cotacao>> buscarValorCotacao(@Path("moeda") String moeda);
}
