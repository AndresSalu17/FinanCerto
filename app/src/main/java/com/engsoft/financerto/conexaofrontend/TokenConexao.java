package com.engsoft.financerto.conexaofrontend;

import static com.engsoft.financerto.conexaofrontend.HttpConexao.getResponse;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.sendRequest;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.setupConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.engsoft.financerto.interfaces.TokenCallback;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class TokenConexao {

    private static final String BASE_URL_REFRESH = "https://finan-certo-api.onrender.com/api/token/refresh/";

    static void salvarTokens(Context context, String access, String refresh){
        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access", access);
        editor.putString("refresh", refresh);
        editor.apply();
    }

    public static void renovarAccessToken(Context context, TokenCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        String refresh = prefs.getString("refresh_token", null);

        if (refresh == null) {
            callback.onError("Refresh token não encontrado.");
            return;
        }

        new Thread(() -> {
            try {
                HttpURLConnection conn = setupConnection(BASE_URL_REFRESH);

                JSONObject json = new JSONObject();
                json.put("refresh", refresh);

                sendRequest(conn, json.toString());

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn);

                if (responseCode == 200) {
                    JSONObject jsonResponse = new JSONObject(response);
                    String newAccessToken = jsonResponse.optString("access", null);
                    if (newAccessToken != null) {
                        salvarTokens(context, newAccessToken, refresh); // Atualiza o access
                        callback.onSuccess(newAccessToken);
                    } else {
                        callback.onError("Novo access token não retornado.");
                    }
                } else {
                    callback.onError("Erro ao renovar token.");
                }
            } catch (Exception e) {
                callback.onError("Erro de conexão: " + e.getMessage());
            }
        }).start();
    }
}
