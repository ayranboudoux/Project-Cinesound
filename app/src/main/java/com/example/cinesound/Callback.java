package com.example.cinesound;
public interface Callback {
    void onSuccess(T resultant);
    void onError(String mensagem);
}
