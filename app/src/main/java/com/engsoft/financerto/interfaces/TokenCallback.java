package com.engsoft.financerto.interfaces;

public interface TokenCallback {
    void onSuccess(String newAccessToken);
    void onError(String error);
}
