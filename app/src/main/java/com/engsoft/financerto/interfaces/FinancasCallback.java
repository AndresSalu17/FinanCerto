package com.engsoft.financerto.interfaces;

public interface FinancasCallback {
    void onSuccess(int ano, int mes, double receitas, double despesas, double balanco);
    void onError(String error);
}
