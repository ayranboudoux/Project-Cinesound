package com.example.cinesound.network;
public interface Callback<T> {
    void onSuccess(T resultant);
    void onError(String mensagem);
}
