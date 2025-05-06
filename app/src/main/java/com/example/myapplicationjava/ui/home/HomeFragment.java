package com.example.myapplicationjava.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationjava.ApiClient;
import com.example.myapplicationjava.ApiService;
import com.example.myapplicationjava.ItemAdapter;
import com.example.myapplicationjava.Record;
import com.example.myapplicationjava.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Record> itemList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView; // make sure recyclerView exists in fragment_home.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        fetchItemsFromApi();

        return root;
    }

    private void fetchItemsFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Record>> call = apiService.getItems();

        call.enqueue(new Callback<List<Record>>() {
            @Override
            public void onResponse(Call<List<Record>> call, Response<List<Record>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList.clear();
                    itemList.addAll(response.body());

                    adapter = new ItemAdapter(itemList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Record>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
