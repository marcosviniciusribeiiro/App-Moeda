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
    EditText edCotacao;
    EditText edReais;
    Button btConverter;
    TextView txResultado;

    RadioButton moeda_dol;
    RadioButton moeda_euro;
    RadioButton moeda_libra;
    RadioButton moeda_iene;
    RadioButton moeda_yuan;

    String chave;
    String codeAPI;
    String simbolo;
    String formato;

    @SuppressLint("MissingInflatedId")
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

        moeda_dol = findViewById(R.id.id_dol);
        moeda_euro = findViewById(R.id.id_euro);
        moeda_libra = findViewById(R.id.id_libra);
        moeda_iene = findViewById(R.id.id_iene);
        moeda_yuan = findViewById(R.id.id_yuan);

        moeda_dol.setOnClickListener(view -> checarCotacao());
        moeda_euro.setOnClickListener(view -> checarCotacao());
        moeda_libra.setOnClickListener(view -> checarCotacao());
        moeda_iene.setOnClickListener(view -> checarCotacao());
        moeda_yuan.setOnClickListener(view -> checarCotacao());

        btConverter.setOnClickListener(view -> 
                converterMoeda());
    }

    private void checarCotacao() {
        if (moeda_euro.isChecked()){
            codeAPI = "EUR-BRL";
            chave = "EURBRL";
            simbolo = "€";
        } else if (moeda_libra.isChecked()){
            codeAPI = "GBP-BRL";
            chave = "GBPBRL";
            simbolo = "£";
        } else if(moeda_iene.isChecked()){
            codeAPI = "JPY-BRL";
            chave = "JPYBRL";
            simbolo = "JP¥";
        } else if(moeda_yuan.isChecked()){
            codeAPI = "CNY-BRL";
            chave = "CNYBRL";
            simbolo = "CN¥";
        }else {
            codeAPI = "USD-BRL";
            chave = "USDBRL";
            simbolo = "$";
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
        formato = "R$ %.2f  → %s %.2f";
        double reais = Double.parseDouble(edReais.getText().toString());
        double valorConversao = reais / Double.parseDouble(edCotacao.getText().toString());
        txResultado.setText(String.format(formato, reais,simbolo, valorConversao));
    }
}