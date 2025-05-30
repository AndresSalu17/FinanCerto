package com.engsoft.financerto.conexaofrontend;

import static com.engsoft.financerto.conexaofrontend.HttpConexao.getResponse;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.sendRequest;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.setupConnection;
import static com.engsoft.financerto.conexaofrontend.TokenConexao.salvarTokens;

import android.content.Context;
import android.util.Log;

import com.engsoft.financerto.interfaces.LoginCallback;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class LoginConexao {

    private static final String BASE_URL_LOGIN = "https://finan-certo-api.onrender.com/api/gettoken/";

    // Método de login com JWT
    public static void loginUsuario(Context context, String email, String senha, LoginCallback callback) {
        Log.d("Login", "Entrou no método loginUsuario!");

        new Thread(() -> {
            try {
                HttpURLConnection conn = setupConnection(BASE_URL_LOGIN);

                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("password", senha);

                sendRequest(conn, json.toString());

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn);

                Log.d("Login", "Response Code: " + responseCode);
                Log.d("Login", "Resposta do servidor: " + response);

                if (responseCode == 200) {
                    JSONObject jsonResponse = new JSONObject(response);
                    String token = jsonResponse.optString("access", null);
                    String refreshToken = jsonResponse.optString("refresh", null);

                    if (token != null && refreshToken != null) {
                        salvarTokens(context, token, refreshToken);
                        callback.onSuccess(token);
                    } else {
                        callback.onError("Token não encontrado na resposta.");
                    }
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    String errorMessage = jsonResponse.optString("detail", "Erro desconhecido!");
                    callback.onError(errorMessage);
                }

            } catch (Exception e) {
                Log.e("Login", "Erro na conexão: ", e);
                callback.onError("Erro de conexão: " + e.getMessage());
            }
        }).start();
    }
}
