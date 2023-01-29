package com.example.digitalupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.digitalupload.adapters.CardItemAdapter;
import com.example.digitalupload.adapters.CategoryAdapter;
import com.example.digitalupload.adapters.SubCategoryAdapter;
import com.example.digitalupload.models.CommonModel;
import com.example.digitalupload.network.RetrofitClient;
import com.example.digitalupload.network.api.CategoryApi;
import com.example.digitalupload.network.api.SubCategoryApi;
import com.example.digitalupload.utils.ToastMsg;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubCategoryAdapter cardAdapter;
    private String category_id;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;
    private EditText searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        progressBar = findViewById(R.id.progressBar);
        coordinatorLayout=findViewById(R.id.coordinator_lyt);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(50);
        recyclerView = findViewById(R.id.card_rv);

        category_id = getIntent().getStringExtra("category_id");

        searchBar = findViewById(R.id.search_text);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Intent intent = new Intent(SubCategoryActivity.this, SearchResultActivity.class);
                    intent.putExtra("search_text", searchBar.getText().toString());
                    startActivity(intent);
                }
                return false;
            }
        });

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubCategoryApi api = retrofit.create(SubCategoryApi.class);
        Call<CommonModel> call = api.getSubCategoryById(category_id);
        call.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                if (response.code() == 200) {
                    progressBar.setVisibility(View.GONE);
                    CommonModel commonModel = response.body();

                    if (commonModel.getCategoryModelsList().size() == 0){
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }else {
                        coordinatorLayout.setVisibility(View.GONE);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(SubCategoryActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    cardAdapter = new SubCategoryAdapter(SubCategoryActivity.this, commonModel.getCategoryModelsList(), category_id);
                    recyclerView.setAdapter(cardAdapter);
                } else {
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(SubCategoryActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                new ToastMsg(SubCategoryActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });



    }
}