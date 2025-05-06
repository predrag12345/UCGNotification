package com.example.myapplicationjava;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {
    @GET("6f4832f5-7451-442c-bab1-82a2a6422834")
    Call<List<Record>> getItems();
}
