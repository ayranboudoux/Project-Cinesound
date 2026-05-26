package com.example.cinesound;
public interface Callback<T> {
    void onSuccess(T resultant);
    void onError(String mensagem);
}
