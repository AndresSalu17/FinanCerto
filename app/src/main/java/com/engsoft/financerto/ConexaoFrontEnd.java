package com.engsoft.financerto;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;

public class ConexaoFrontEnd {

    private static final String BASE_URL_CADASTRO = "https://finan-certo-api.onrender.com/cadastrousuario/";
    private static final String BASE_URL_LOGIN = "https://finan-certo-api.onrender.com/login/";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "28012001dre";

    private static HttpURLConnection setupConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoOutput(true);

        // Adiciona autenticação básica
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
        conn.setRequestProperty("Authorization", encodedAuth);

        return conn;
    }

    private static void sendRequest(HttpURLConnection conn, String jsonInput) throws IOException {
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.writeBytes(jsonInput);
            wr.flush();
        }
    }

    private static String getResponse(HttpURLConnection conn) throws IOException {
        InputStream inputStream = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public static void cadastrarUsuario(String nomeCompleto, String email, String senha) {
        Log.d("Cadastro", "Método cadastrarUsuario foi chamado");

        new Thread(() -> {
            try {
                HttpURLConnection conn = setupConnection(BASE_URL_CADASTRO);

                // Montando JSON de forma mais segura
                JSONObject json = new JSONObject();
                json.put("USUARIO_NOME_COMPLETO", nomeCompleto);
                json.put("USUARIO_EMAIL", email);
                json.put("USUARIO_SENHA", senha);

                sendRequest(conn, json.toString());

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn);

                Log.d("Cadastro", "Response Code: " + responseCode);
                Log.d("Cadastro", "Resposta do servidor: " + response);

            } catch (Exception e) {
                Log.e("Cadastro", "Erro na conexão: ", e);
            }
        }).start();
    }

    public interface LoginCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public static void loginUsuario(String email, String senha, LoginCallback callback) {
        Log.d("Login", "Entrou no método loginUsuario!");

        new Thread(() -> {
            try {
                HttpURLConnection conn = setupConnection(BASE_URL_LOGIN);

                // Criando JSON de forma correta
                JSONObject json = new JSONObject();
                json.put("USUARIO_EMAIL", email);
                json.put("USUARIO_SENHA", senha);

                sendRequest(conn, json.toString());

                int responseCode = conn.getResponseCode();
                String response = getResponse(conn);

                Log.d("Login", "Response Code: " + responseCode);
                Log.d("Login", "Resposta do servidor: " + response);

                if (responseCode == 200) {
                    callback.onSuccess("Login realizado com sucesso!");
                } else {
                    JSONObject jsonResponse = new JSONObject(response);
                    String errorMessage = jsonResponse.optString("error", "Erro desconhecido!");
                    callback.onError(errorMessage);
                }

            } catch (Exception e) {
                Log.e("Login", "Erro na conexão: ", e);
            }
        }).start();
    }
}
