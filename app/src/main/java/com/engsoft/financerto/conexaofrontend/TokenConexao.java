package com.engsoft.financerto.conexaofrontend;

import static com.engsoft.financerto.conexaofrontend.HttpConexao.getResponse;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.sendRequest;
import static com.engsoft.financerto.conexaofrontend.HttpConexao.setupConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.engsoft.financerto.interfaces.TokenCallback;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class TokenConexao {

    private static final String BASE_URL_REFRESH = "https://finan-certo-api.onrender.com/api/token/refresh/";

    static void salvarTokens(Context context, String access, String refresh){
        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putString("access", access);
        editor.putString("refresh", refresh);
        editor.apply();
    }

    public static String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        return prefs.getString("access", null);
    }

    public static void limparTokens(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Log.d("Token", "Tokens removidos.");
    }


}
