package com.engsoft.financerto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;

import android.util.Base64;
import android.util.Log;


public class ConexaoFrontEnd {

    private static final String BASE_URL = "https://finan-certo-api.onrender.com/cadastrousuario/";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "28012001dre";

    public static void cadastrarUsuario(String nome, String email, String senha) {
        Log.d("Cadastro", "Método cadastrarUsuario foi chamado");
        new Thread(() -> {
            try {
                URL urlCadastro = new URL(BASE_URL);
                HttpURLConnection conn = (HttpURLConnection) urlCadastro.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String auth = USERNAME + ":" + PASSWORD;
                String encodedAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
                conn.setRequestProperty("Authorization", encodedAuth);

                conn.setDoOutput(true);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

                String jsonInputString = "{"
                        + "\"USUARIO_NOME_COMPLETO\": \"" + nome + "\","
                        + "\"USUARIO_EMAIL\": \"" + email + "\","
                        + "\"USUARIO_SENHA\": \"" + senha + "\""
                        + "}";

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(jsonInputString);
                wr.flush();
                wr.close();

                int responseCode = conn.getResponseCode();
                Log.d("Cadastro", "Response Code: " + responseCode);

                InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d("Cadastro", "Resposta do servidor: " + response.toString());
                }

            } catch (IOException e) {
                Log.e("Cadastro", "Erro na conexão: ", e);
            }
        }).start();
    }
}