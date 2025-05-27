package com.engsoft.financerto.fluxoapp;

import static com.engsoft.financerto.conexaofrontend.FinancasConexao.buscarFinancasUsuario;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;


import com.engsoft.financerto.R;
import com.engsoft.financerto.interfaces.FinancasCallback;
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

public class TelaPrincipal extends AppCompatActivity {

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_principal);

        pieChart = findViewById(R.id.pieChart);

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
    }

    private void atualizarGrafico(double receitas, double despesas) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (despesas > 0) {
            entries.add(new PieEntry((float) despesas, "Despesas"));
        } else {
            entries.add(new PieEntry(0));
        }
        if (receitas > 0) {
            entries.add(new PieEntry((float) receitas, "Receitas"));
        } else {
            entries.add(new PieEntry(0));
        }

        PieDataSet dataSet = new PieDataSet(entries,"");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
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
