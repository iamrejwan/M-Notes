package com.example.digitalupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.digitalupload.adapters.CardItemAdapter;
import com.example.digitalupload.models.CommonModel;
import com.example.digitalupload.models.UserModelList;
import com.example.digitalupload.network.RetrofitClient;
import com.example.digitalupload.network.api.CategoryApi;
import com.example.digitalupload.network.api.UserApi;
import com.example.digitalupload.utils.ToastMsg;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardItemAdapter itemAdapter;
    private String category_id;
    private String sub_category_id;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;
    private EditText searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        progressBar = findViewById(R.id.progressBar);
        coordinatorLayout=findViewById(R.id.coordinator_lyt);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(50);
        recyclerView = findViewById(R.id.card_rv);



        category_id = getIntent().getStringExtra("category_id");
        sub_category_id =getIntent().getStringExtra("sub_category_id");


        searchBar = findViewById(R.id.search_text);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Intent intent = new Intent(UsersActivity.this, SearchResultActivity.class);
                    intent.putExtra("search_text", searchBar.getText().toString());
                    startActivity(intent);
                }
                return false;
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserApi api = retrofit.create(UserApi.class);
        Call<UserModelList> call = api.getUserByCategory(category_id, sub_category_id);
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(UsersActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    itemAdapter = new CardItemAdapter(UsersActivity.this, userModelList.getUserModelsList());
                    recyclerView.setAdapter(itemAdapter);
                } else {
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(UsersActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<UserModelList> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                new ToastMsg(UsersActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });

    }
}