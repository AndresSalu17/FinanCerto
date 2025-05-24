package com.engsoft.financerto.interfaces;

public interface LoginCallback {
    void onSuccess(String token); // Token JWT
    void onError(String error);
}
