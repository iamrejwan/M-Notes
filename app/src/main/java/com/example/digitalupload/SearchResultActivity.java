package com.example.digitalupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.digitalupload.adapters.CardItemAdapter;
import com.example.digitalupload.models.UserModelList;
import com.example.digitalupload.network.RetrofitClient;
import com.example.digitalupload.network.api.UserApi;
import com.example.digitalupload.utils.ToastMsg;

import org.jetbrains.annotations.NotNull;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardItemAdapter itemAdapter;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        String searchText = getIntent().getStringExtra("search_text");


        progressBar = findViewById(R.id.progressBar);
        coordinatorLayout=findViewById(R.id.coordinator_lyt);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(50);
        recyclerView = findViewById(R.id.card_rv);

        searchResult(searchText);



    }

    private void searchResult(String searchText){
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserApi api = retrofit.create(UserApi.class);
        Call<UserModelList> call = api.searchUser(searchText);
        call.enqueue(new Callback<UserModelList>() {
            @Override
            public void onResponse(@NotNull Call<UserModelList> call, @NotNull Response<UserModelList> response) {
                if (response.code() == 200) {

                    progressBar.setVisibility(View.GONE);

                    UserModelList userModelList = response.body();
                    if (userModelList.getUserModelsList().size() == 0){
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    itemAdapter = new CardItemAdapter(SearchResultActivity.this, userModelList.getUserModelsList());
                    recyclerView.setAdapter(itemAdapter);
                } else {
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(SearchResultActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<UserModelList> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                new ToastMsg(SearchResultActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
