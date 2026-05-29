package com.example.cinesound.network;
public interface Callback<T> {
    void onSuccess(T resultado);
    void onError(String mensagem);
}
