package com.exemplo.ewm.appmoeda;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity  extends AppCompatActivity{
    // declarando as variáveis para os componentes do app
    EditText edCotacao;
    EditText edReais;
    Button btConverter;
    TextView txResultado;
    RadioButton moeda_dol;
    RadioButton moeda_eur;
    RadioButton moeda_btc;

    String tipo;
    String chave;
    String codeAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            checarCotacao();
            return insets;
        });

        edCotacao = findViewById(R.id.id_val_cotacao);
        edReais = findViewById(R.id.id_val_reais);
        btConverter = findViewById(R.id.id_btn_converter);
        txResultado = findViewById(R.id.id_resultado);

        moeda_dol = findViewById(R.id.rd_dol);
        moeda_eur = findViewById(R.id.rd_euro);
        moeda_btc = findViewById(R.id.rd_btc);

        moeda_dol.setOnClickListener(view -> checarCotacao());
        moeda_eur.setOnClickListener(view -> checarCotacao());
        moeda_btc.setOnClickListener(view -> checarCotacao());

        btConverter.setOnClickListener(view ->
                converterMoeda());

    }

    private void checarCotacao() {
        if (moeda_eur.isChecked()){
            codeAPI = "EUR-BRL";
            chave = "EURBRL";
            tipo = "Euros";
        } else if (moeda_btc.isChecked()){
            codeAPI = "BTC-BRL";
            chave = "BTCBRL";
            tipo = "Bitcoins";
        } else {
            codeAPI = "USD-BRL";
            chave = "USDBRL";
            tipo = "Dólares";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://economia.awesomeapi.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServicoAPI servicoAPI = retrofit.create(ServicoAPI.class);
        Call<Map<String, Cotacao>> chamada = servicoAPI.buscarValorCotacao(codeAPI);
        chamada.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Cotacao>> call, @NonNull Response<Map<String, Cotacao>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Cotacao cotacao = response.body().get(chave);
                    assert cotacao != null;
                    edCotacao.setText(cotacao.getValor());
                } else {
                    txResultado.setText(R.string.resultado_de_erro);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Cotacao>> call, @NonNull Throwable t) {
                txResultado.setText(String.format("Erro: \n%s", t.getMessage()));
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void converterMoeda() {
        if(edReais.getText().isEmpty()){
            edReais.setError("Digite algum valor!");
            return;
        }
        double reais = Double.parseDouble(edReais.getText().toString());
        double valorConversao = reais / Double.parseDouble(edCotacao.getText().toString());
        txResultado.setText(String.format("Resultado: \n%.2f %s", valorConversao, tipo));
    }
}