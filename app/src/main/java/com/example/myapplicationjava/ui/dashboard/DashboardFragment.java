package com.example.myapplicationjava.ui.dashboard;

import static android.content.Intent.getIntent;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationjava.ApiClient;
import com.example.myapplicationjava.ApiService;
import com.example.myapplicationjava.ItemAdapter;
import com.example.myapplicationjava.R;
import com.example.myapplicationjava.Record;
import com.example.myapplicationjava.databinding.FragmentDashboardBinding;
import com.example.myapplicationjava.databinding.FragmentHomeBinding;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Record> itemList = new ArrayList<>();
private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        savePhoneIdIfNeeded(); // Save unique phone ID if not already saved
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (isFirstTime()) {
            // First time: show checkboxes + button
            showFirstTimeUI(container);
//            setFirstTimeFalse(); // Mark as not first time anymore
        } else {
            // Not first time: normal behavior (showing recyclerview or text)
            binding.recyclerView.setVisibility(VISIBLE);


            recyclerView = binding.recyclerView; // make sure recyclerView exists in fragment_home.xml
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            fetchItemsFromApi();

        }

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    private void showFirstTimeUI(ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        binding.linearLayoutContainer.removeAllViews(); // Make sure it's empty first

        for (int i = 1; i <= 20; i++) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText("Option " + i);
            binding.linearLayoutContainer.addView(checkBox);
        }

        //request za listu predmeta i na osnovu chekiranih jedan post request

        Button submitButton = new Button(getContext());
        submitButton.setText("Submit");
        submitButton.setOnClickListener(v -> {
            JSONArray selectedItems = new JSONArray();
//            selectedItems.put(KEY_PHONE_ID.toString()); treba li id da salje
            for (int i = 0; i < binding.linearLayoutContainer.getChildCount(); i++) {
                View child = binding.linearLayoutContainer.getChildAt(i);
                if (child instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) child;
                    if (checkBox.isChecked()) {
                        selectedItems.put(checkBox.getText().toString());
                    }
                }
            }
            setFirstTimeFalse();
            Toast.makeText(getContext(), selectedItems.toString(), Toast.LENGTH_LONG).show();


            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.navigation_home);
            }, 1000);
        });


        binding.linearLayoutContainer.addView(submitButton);
    }


    private static final String PREF_NAME = "my_prefs";
    private static final String KEY_FIRST_TIME = "isFirstTime";
    private static final String KEY_PHONE_ID = "phoneId";

    private boolean isFirstTime() {
        return requireActivity()
                .getSharedPreferences(PREF_NAME, 0)
                .getBoolean(KEY_FIRST_TIME, true);
    }

    private void setFirstTimeFalse() {
        requireActivity()
                .getSharedPreferences(PREF_NAME, 0)
                .edit()
                .putBoolean(KEY_FIRST_TIME, false)
                .apply();
    }

    private void savePhoneIdIfNeeded() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
        if (!prefs.contains(KEY_PHONE_ID)) {
            String androidId = android.provider.Settings.Secure.getString(
                    requireActivity().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID
            );
            prefs.edit().putString(KEY_PHONE_ID, androidId).apply();
        }
    }





}