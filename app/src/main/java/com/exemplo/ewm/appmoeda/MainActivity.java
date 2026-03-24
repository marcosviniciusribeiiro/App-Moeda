package com.exemplo.ewm.appmoeda;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText edCotacao;
    EditText edReais;
    Button btConverter;
    TextView txResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //instanciar
        edCotacao = findViewById(R.id.id_val_cotacao);
        edReais = findViewById(R.id.id_val_reais);
        btConverter = findViewById(R.id.id_btn_converter);
        txResultado = findViewById(R.id.id_resultado);

        btConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                converter();
                /*
                NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
                double cotacao = Double.parseDouble(edCotacao.getText().toString());
                double reais = Double.parseDouble(edReais.getText().toString());
                double valorConversao = reais / cotacao;
                txResultado.setText("Resultado: "+formato.format(valorConversao)+" Dólares ");*/

            }
        });

    }

    private void converter() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://economia.awesomeapi.com.br/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ServicoAPI servico = retrofit.create(ServicoAPI.class);
            Call<Map<String, Cotacao>> chamada = servico.buscarValorCotacao();
            chamada.enqueue(new Callback<Map<String, Cotacao>>() {
                @Override
                public void onResponse(Call<Map<String, Cotacao>> call, Response<Map<String, Cotacao>> response) {
                    if (response.isSuccessful()){
                        Cotacao cotacao = response.body().get("USDBRL");
                        edCotacao.setText(cotacao.getValor());
                        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
                        double reais = Double.parseDouble(edReais.getText().toString());
                        double valorConversao = reais / Double.parseDouble(cotacao.getValor());
                        txResultado.setText(String.format("Resultado: %s Dólares ", formato.format(valorConversao)));
                    } else {
                        txResultado.setText("Resultado:\nErro ao buscar a cotação");
                    }


                }

                @Override
                public void onFailure(Call<Map<String, Cotacao>> call, Throwable t) {
                    txResultado.setText("Erro na requisição: "+t.getMessage());
                }
            });
    }
}