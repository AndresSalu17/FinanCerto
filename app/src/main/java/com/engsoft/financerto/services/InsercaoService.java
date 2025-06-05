package com.engsoft.financerto.services;

import static com.engsoft.financerto.conexaofrontend.HttpConexao.getResponse;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.sendRequest;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.setupConnection;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.engsoft.financerto.interfaces.InsercaoCallback;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsercaoService {

    private static final String BASE_URL_INSERCAO = "https://finan-certo-api.onrender.com/financasusuario/";

    public static void insercaoReceitasDespesas(Context context, double receitas, double despesas, int mes, int ano, InsercaoCallback callback ){
        Log.d("Inserção", "Entrou no método de inserção.");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(BASE_URL_INSERCAO);

                String token = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        .getString("access", null);
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }


                JSONObject json = new JSONObject();
                json.put("FINANCAS_RECEITAS", receitas);
                json.put("FINANCAS_DESPESAS", despesas);
                json.put("MES_ATUAL", mes);
                json.put("FINANCAS_ANO", ano);

                sendRequest(conn, json.toString());

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn);

                Log.d("Inserção", "Response Code: " + responseCode);
                Log.d("Inseção", "Reposta do servidor: " + response);

                handler.post(() -> {
                   try {
                       if(responseCode == 200){
                           callback.onSuccess("Inserção realizada com sucesso!");
                       }
                       else {
                           callback.onError("Erro na inserção das finanças!");
                       }

                   } catch (Exception e) {
                       callback.onSuccess("Erro ao processar a resposta: " + e.getMessage());
                   }
                });


            } catch (Exception e) {
                Log.e("Inserção", "Erro na conexão: ", e);
                callback.onError("Erro na conexão: " + e.getMessage());
            }
        });

    }
}
