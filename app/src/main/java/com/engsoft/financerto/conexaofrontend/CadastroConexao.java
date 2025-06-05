package com.engsoft.financerto.conexaofrontend;

import static com.engsoft.financerto.conexaofrontend.HttpConexao.getResponse;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.sendRequest;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.setupConnection;
import static com.engsoft.financerto.conexaofrontend.TokenConexao.salvarTokens;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;

import com.engsoft.financerto.interfaces.CadastroCallback;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CadastroConexao {

    private static final String BASE_URL_CADASTRO = "https://finan-certo-api.onrender.com/usuarios/";

    // Método para cadastrar um usuário
    // Método para cadastrar um usuário
    public static void cadastrarUsuario(Context context, String nome, String sobreNome, String email, String senha, CadastroCallback callback) {

        Log.d("Cadastro", "Método cadastrarUsuario foi chamado");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                HttpURLConnection conn = setupConnection(BASE_URL_CADASTRO);

                JSONObject json = new JSONObject();
                json.put("first_name", nome);
                json.put("last_name", sobreNome);
                json.put("email", email);
                json.put("password", senha);

                sendRequest(conn, json.toString());

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn);

                Log.d("Cadastro", "Response Code: " + responseCode);
                Log.d("Cadastro", "Resposta do servidor: " + response);

                handler.post(() -> {
                    try {
                        if (responseCode == 201) {
                            JSONObject jsonResponse = new JSONObject(response);
                            String token = jsonResponse.optString("access", null);
                            String refreshToken = jsonResponse.optString("refresh", null);

                            if (token != null && refreshToken != null) {
                                salvarTokens(context, token, refreshToken);
                                Log.d("Cadastro", "Tokens salvos após cadastro.");
                            }

                            callback.onSuccess(response);
                        } else {
                            callback.onError("Erro: " + response);
                        }
                    } catch (Exception e) {
                        callback.onError("Erro ao processar resposta do cadastro: " + e.getMessage());
                    }
                });


            } catch (Exception e) {
                Log.e("Cadastro", "Erro na conexão: ", e);
                handler.post(() -> callback.onError("Erro na conexão: " + e.getMessage()));
            }
        });
    }
}
