package com.exemplo.ewm.appmoeda;

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

import java.text.NumberFormat;
import java.util.Locale;
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
    RadioButton moeda_lbr;

    String tipo;

/*
  Evoluir o projeto para
- O valor da cotação já aparecer na tela assim que o aplicativo for inicializado
- Inserir a escolha de outras moedas para conveter para o real
*/

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

        // instanciando as variáveis do app
        edCotacao = findViewById(R.id.id_val_cotacao);
        edReais = findViewById(R.id.id_val_reais);
        btConverter = findViewById(R.id.id_btn_converter);
        txResultado = findViewById(R.id.id_resultado);

        moeda_dol = findViewById(R.id.rd_dol);
        moeda_eur = findViewById(R.id.rd_euro);
        moeda_lbr = findViewById(R.id.rd_libra);

        btConverter.setOnClickListener(view ->
                converterMoeda());
    }

    private void converterMoeda() {
        // classifica o tipo de moeda de acordo com o radio selecionado
        if (moeda_eur.isChecked()){
            tipo = "Euros";
        } else if (moeda_lbr.isChecked()){
            tipo = "Libras";
        } else {
            tipo = "Dólares";
        }

        // URL da api de moeda
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://economia.awesomeapi.com.br/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServicoAPI servico = retrofit.create(ServicoAPI.class);
        Call<Map<String, Cotacao>> chamada = servico.buscarValorCotacao();
        chamada.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Cotacao>> call, @NonNull Response<Map<String, Cotacao>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Cotacao cotacao = response.body().get("USDBRL");
                    assert cotacao != null;
                    edCotacao.setText(cotacao.getValor());
                    NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
                    double reais = Double.parseDouble(edReais.getText().toString());
                    double valorConversao = reais / Double.parseDouble(edCotacao.getText().toString());
                    txResultado.setText("Resultado: \n" + formato.format(valorConversao)+ " " + tipo);
                } else {
                    txResultado.setText(R.string.resultado_de_erro);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Cotacao>> call, @NonNull Throwable t) {
                txResultado.setText(String.format("Erro na requisição: \n%s", t.getMessage()));
            }
        });
    }
}


//// codigo simples de conversão:
/*
                NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
                double cotacao = Double.parseDouble(edCotacao.getText().toString());
                double reais = Double.parseDouble(edReais.getText().toString());
                double valorConversao = reais / cotacao;
                txResultado.setText(String.format("Resultado: %s Dólares", formato.format(valorConversao);
 */