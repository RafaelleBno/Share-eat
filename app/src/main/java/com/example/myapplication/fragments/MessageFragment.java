package com.example.myapplication.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.fragments.Message; // Assurez-vous que le bon package est importé

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private EditText searchBar;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        // Infla le layout du fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Initialisation des vues
        searchBar = view.findViewById(R.id.searchBar);
        recyclerView = view.findViewById(R.id.messagesRecyclerView);

        // Configurer le RecyclerView avec un LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Charger les messages fictifs
        loadMessages();

        // Initialiser l'adaptateur
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        // Ajouter un TextWatcher pour la barre de recherche
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Rien à faire ici
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterMessages(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Rien à faire ici
            }
        });

        return view;
    }

    // Méthode pour charger les messages fictifs
    private void loadMessages() {
        messageList.add(new Message("Ramin", "Status 1", "10:00 AM", R.drawable.avatar_placeholder));
        messageList.add(new Message("Rabia", "Status 2", "10:30 AM", R.drawable.avatar_placeholder));
        messageList.add(new Message("John", "Status 3", "11:00 AM", R.drawable.avatar_placeholder));
        messageList.add(new Message("Maria", "Status 4", "11:30 AM", R.drawable.avatar_placeholder));
    }

    // Méthode pour filtrer les messages en fonction de la recherche
    private void filterMessages(String query) {
        List<Message> filteredMessages = new ArrayList<>();
        for (Message message : messageList) {
            if (message.getUserName().toLowerCase().contains(query.toLowerCase())) {
                filteredMessages.add(message);
            }
        }
        // Mettre à jour les données dans l'adaptateur
        adapter = new MessageAdapter(filteredMessages);
        recyclerView.setAdapter(adapter);
    }
}


