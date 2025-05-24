package com.engsoft.financerto.conexaofrontend;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConexao {
    // Método genérico para configurar a conexão HTTP
    static HttpURLConnection setupConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoOutput(true);
        return conn;
    }

    // Envia o corpo JSON para a conexão
    static void sendRequest(HttpURLConnection conn, String jsonInput) throws IOException {
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.writeBytes(jsonInput);
            wr.flush();
        }
    }

    // Lê a resposta do servidor
    static String getResponse(HttpURLConnection conn) throws IOException {
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
}
