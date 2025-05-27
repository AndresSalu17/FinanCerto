package com.engsoft.financerto.conexaofrontend;

import static com.engsoft.financerto.conexaofrontend.HttpConexao.getResponse;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.sendRequest;

import android.content.Context;
import android.util.Log;

import com.engsoft.financerto.interfaces.FinancasCallback;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;

public class FinancasConexao {

    private static final String BASE_URL_FINANCAS_USUARIO = "https://finan-certo-api.onrender.com/financasusuario/";

    public static void atualizarFinancasUsuario(Context context, int anoFinancas, int mesFinancas, double receitas, double despesas, double balancoFinancas){

        Log.d("Finanças", "Entrando no método de finanças.");

        new Thread(() -> {
            try{
                URL url = new URL(BASE_URL_FINANCAS_USUARIO);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PATCH");
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("FINANCAS_ANO", anoFinancas);
                json.put("MES_ATUAL", mesFinancas);
                json.put("FINANCAS_RECEITAS", receitas);
                json.put("FINANCAS_DESPESAS", despesas);
                json.put("FINANCAS_BALANCO", balancoFinancas);

                sendRequest(conn, json.toString());

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn); // Use sua função para ler o InputStream

                Log.d("Finanças", "Response PUT: " + responseCode + " | " + response);

            }catch (Exception e){
                Log.e("Finanças", "Erro na conexão de finanças: ", e);
            }
        }).start();
    }

    public static void buscarFinancasUsuario(Context context, FinancasCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL_FINANCAS_USUARIO);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                String token = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        .getString("access", null);
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn);

                Log.d("Finanças", "Response GET: " + responseCode + " | " + response);

                if (responseCode == 200) {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray results = jsonResponse.getJSONArray("results");

                    if (results.length() > 0) {
                        JSONObject dados = results.getJSONObject(0);

                        int ano = dados.optInt("FINANCAS_ANO");
                        int mes = dados.optInt("MES_ATUAL");
                        double receitas = dados.optDouble("FINANCAS_RECEITAS");
                        double despesas = dados.optDouble("FINANCAS_DESPESAS");
                        double balanco = dados.optDouble("FINANCAS_BALANCO");

                        callback.onSuccess(ano, mes, receitas, despesas, balanco);
                    } else {
                        callback.onSuccess(0, 0, 0.0, 0.0, 0.0); // Nenhum dado encontrado
                    }
                } else {
                    callback.onError("Erro ao buscar dados financeiros.");
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e("Finanças", "Erro na conexão de finanças (GET): ", e);
                callback.onError("Erro de conexão: " + e.getMessage());
            }
        }).start();
    }
}
