package com.example.cinesound;

import android.icu.text.UFormat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirebaseRepository {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public FirebaseRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    //Autenticação

    public void cadastrar(String email, String senha, OnSuccessListener<AuthResult> onSucesso, OnFailureListener onErro) {
        auth
                .createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);
    }

    public void login(String email, String senha, OnSuccessListener<AuthResult> onSucesso, OnFailureListener onErro) {
        auth
                .signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);
    }

    public void recuperarSenha(String email, OnSuccessListener<Void> onSucesso, OnFailureListener onErro) {
        auth
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getUsuarioAtual() {
        return auth.getCurrentUser();
    }

    //Perfil do usuario

    public void salvarPerfil(String uid, String nome, String email, OnSuccessListener<Void> onSucesso, OnFailureListener onErro) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("uid", uid);
        dados.put("nome", nome);
        dados.put("email", email);
        dados.put("avatarUrl", "");
        dados.put("dataCadastro", Timestamp.now());

        db.collection("usuarios")
                .document(uid)
                .set(dados)
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);
    }

    public void buscarPerfil(String uid, OnSuccessListener<DocumentSnapshot> onSucesso, OnFailureListener onErro) {
        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);
    }

    //Classificações

    public void salvarClassificacao(TituloItem titulo, String classificacao, OnSuccessListener<Void> onSucesso, OnFailureListener onErro) {
        String uid = auth.getCurrentUser().getUid();
        String docId = uid + "_" + titulo.tmdbId;


        Map<String, Object> dados = new HashMap<>();

        dados.put("uid", uid);
        dados.put("tmdbId", titulo.tmdbId);
        dados.put("titulo", titulo.titulo);
        dados.put("poster", titulo.poster);
        dados.put("tipo", titulo.tipo);
        dados.put("generos", titulo.generos);
        dados.put("notaTmdb", titulo.notaTmdb);
        dados.put("classificacao", classificacao);
        dados.put("dataClassificacao", Timestamp.now());

        db.collection("classificacoes")
                .document(docId)
                .set(dados)
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);
    }


    public void buscarClassificacoes(OnSuccessListener<QuerySnapshot> onSucesso, OnFailureListener onErro) {
        String uid = auth.getCurrentUser().getUid();

        db.collection("classificacoes")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);
    }


    public void buscarGenerosFavoritos(OnSuccessListener<QuerySnapshot> onSucesso, OnFailureListener onErro) {
        String uid = auth.getCurrentUser().getUid();

        db.collection("classificacoes")
                .whereEqualTo("uid", uid)
                .whereIn("classificacao", Arrays.asList("amei", "gostei"))
                .get()
                .addOnSuccessListener(onSucesso)
                .addOnFailureListener(onErro);

    }

    public List<Integer> extrairGenerosMaisFrequentes(QuerySnapshot snapshot) {
        Map<Integer, Integer> contagem = new HashMap<>();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            List<Long> generos = (List<Long>) doc.get("generos");
            if (generos == null) continue;
            for (Long g : generos) {
                int id = g.intValue();
                contagem.put(id, contagem.getOrDefault(id, 0) + 1);
            }
        }
        //Ordena pelos mais frequentes e retorna os 3 principais
        return contagem.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
