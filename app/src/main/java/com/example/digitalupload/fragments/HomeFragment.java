package com.example.digitalupload.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.digitalupload.MainActivity;
import com.example.digitalupload.R;
import com.example.digitalupload.SearchResultActivity;
import com.example.digitalupload.UserAddActivity;
import com.example.digitalupload.adapters.CategoryAdapter;
import com.example.digitalupload.models.CommonModel;
import com.example.digitalupload.network.RetrofitClient;
import com.example.digitalupload.network.api.CategoryApi;
import com.example.digitalupload.utils.ToastMsg;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private MainActivity activity;
    private RecyclerView recyclerView;
    private CategoryAdapter cAdapter;
    private FloatingActionButton fab;
    private EditText searchBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getWindow().getDecorView().setSystemUiVisibility(256);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));

        // Inflate the layout for this fragment
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(activity);
        super.onViewCreated(view, savedInstanceState);

        //Keep returning false to Should Keep On Screen until ready to begin.
        splashScreen.setKeepOnScreenCondition(() -> true);

        recyclerView = view.findViewById(R.id.card_rv);
        fab = view.findViewById(R.id.fab);
        searchBar = view.findViewById(R.id.search_text);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Intent intent = new Intent(activity, SearchResultActivity.class);
                    intent.putExtra("search_text", searchBar.getText().toString());
                    startActivity(intent);
                }
                return false;
            }
        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(activity, UserAddActivity.class);
            startActivity(intent);
        });

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CategoryApi api = retrofit.create(CategoryApi.class);
        Call<CommonModel> call = api.getHomeCategory();
        call.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                if (response.code() == 200) {

                    CommonModel commonModel = response.body();

                    recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setNestedScrollingEnabled(false);
                    cAdapter = new CategoryAdapter(activity, commonModel.getCategoryModelsList());
                    recyclerView.setAdapter(cAdapter);
                    splashScreen.setKeepOnScreenCondition(() -> false );
                } else {
                    new ToastMsg(activity).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                Log.d("Nikku", "onFailure:2 "+t);
                new ToastMsg(activity).toastIconError(getString(R.string.error_toast));
            }
        });
    }
}