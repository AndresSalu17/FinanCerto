package com.engsoft.financerto.fluxoapp;

import static com.engsoft.financerto.conexaofrontend.FinancasConexao.buscarFinancasUsuario;
import static com.engsoft.financerto.services.InsercaoService.insercaoReceitasDespesas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.engsoft.financerto.R;
import com.engsoft.financerto.interfaces.FinancasCallback;
import com.engsoft.financerto.interfaces.InsercaoCallback;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class TelaPrincipal extends AppCompatActivity {

    PieChart pieChart;
    private Button btnInserir;
    private EditText editReceitas;
    private EditText editDespesas;
    private ImageView imgUser;
    private ImageView imgNotificacao;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_principal);

        pieChart = findViewById(R.id.pieChart);
        btnInserir = findViewById(R.id.btn_inserir);
        editReceitas = findViewById(R.id.edit_entrada_receitas);
        editDespesas = findViewById(R.id.edit_entrada_despesas);
        imgUser = findViewById(R.id.img_user);
        imgNotificacao = findViewById(R.id.img_notficacao);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();

        buscarFinancasUsuario(this, new FinancasCallback() {
            @Override
            public void onSuccess(int ano, int mes, double receitas, double despesas, double balanco) {
                runOnUiThread(() -> {
                    atualizarGrafico(receitas, despesas);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(TelaPrincipal.this, "Erro ao buscar finanças: " + error, Toast.LENGTH_LONG).show()
                );
            }
        });

        imgUser.setOnClickListener(view ->{
            Intent intent = new Intent(this, ConfiguracaoUsuario.class);
            startActivity(intent);
        });

        imgNotificacao.setOnClickListener(view -> {
            Intent intent = new Intent(this, NotificacoesUsuario.class);
            startActivity(intent);
        });

        btnInserir.setOnClickListener(view -> {
            String strInserirReceitas = editReceitas.getText().toString().trim();
            String strInserirDespesas = editDespesas.getText().toString().trim();

            if (strInserirReceitas.isEmpty() && strInserirDespesas.isEmpty()) {
                return;
            }


            if (strInserirReceitas.isEmpty()) {
                strInserirReceitas = "0.0";
            }

            if (strInserirDespesas.isEmpty()) {
                strInserirDespesas = "0.0";
            }


            double inserirReceitas = Double.parseDouble(strInserirReceitas);
            double inserirDespesas = Double.parseDouble(strInserirDespesas);

            Calendar calendar = Calendar.getInstance();
            int mesAtual = calendar.get(Calendar.MONTH) + 1; // Janeiro = 0
            int anoAtual = calendar.get(Calendar.YEAR);




            insercaoReceitasDespesas(this, inserirReceitas, inserirDespesas, mesAtual, anoAtual, new InsercaoCallback() {
                @Override
                public void onSuccess(String success) {
                    Log.d("Inserção", success);

                    runOnUiThread(() -> {
                        // Limpa os campos após inserir
                        editReceitas.setText("");
                        editDespesas.setText("");
                    });

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            buscarFinancasUsuario(TelaPrincipal.this, new FinancasCallback() {
                                @Override
                                public void onSuccess(int ano, int mes, double receitas, double despesas, double balanco) {
                                    runOnUiThread(() -> atualizarGrafico(receitas, despesas));
                                }

                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() ->
                                            Toast.makeText(TelaPrincipal.this, "Erro ao atualizar gráfico: " + error, Toast.LENGTH_SHORT).show()
                                    );
                                }
                            });

                            }, 500); // 500ms

                }

                @Override
                public void onError(String error) {
                    Log.e("Inserção", error);
                    runOnUiThread(() -> {
                        // Limpa os campos após inserir
                        editReceitas.setText("");
                        editDespesas.setText("");
                    });

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        buscarFinancasUsuario(TelaPrincipal.this, new FinancasCallback() {
                            @Override
                            public void onSuccess(int ano, int mes, double receitas, double despesas, double balanco) {
                                runOnUiThread(() -> atualizarGrafico(receitas, despesas));
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() ->
                                        Toast.makeText(TelaPrincipal.this, "Erro ao atualizar gráfico: " + error, Toast.LENGTH_SHORT).show()
                                );
                            }
                        });

                    }, 500); // 500ms
                }
            });
        });
    }

    private void atualizarGrafico(double receitas, double despesas) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (receitas > 0) {
            entries.add(new PieEntry((float) receitas, "Receitas"));
        } else {
            entries.add(new PieEntry(0));
        }

        if (despesas > 0) {
            entries.add(new PieEntry((float) despesas, "Despesas"));
        } else {
            entries.add(new PieEntry(0));
        }


        PieDataSet dataSet = new PieDataSet(entries,null);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setColors(Color.BLUE, Color.RED);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(16f);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Finanças");
        pieChart.setBackgroundColor(Color.TRANSPARENT);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
