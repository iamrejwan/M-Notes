package com.example.digitalupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.digitalupload.adapters.SubCategoryAdapter;
import com.example.digitalupload.models.Auth;
import com.example.digitalupload.models.CommonModel;
import com.example.digitalupload.network.RetrofitClient;
import com.example.digitalupload.network.api.LoginApi;
import com.example.digitalupload.network.api.SubCategoryApi;
import com.example.digitalupload.utils.ToastMsg;
import com.hanks.passcodeview.PasscodeView;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Login extends AppCompatActivity {

    PasscodeView passcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DigitalUpload);
        setContentView(R.layout.activity_login);

        passcodeView = findViewById(R.id.passcode_view);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        LoginApi api = retrofit.create(LoginApi.class);
        Call<Auth> call = api.getAuth();
        call.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(@NotNull Call<Auth> call, @NotNull Response<Auth> response) {
                if (response.code() == 200) {
                    Auth auth = response.body();
                    String PASS = new String(Base64.decode(auth.getPassword(),Base64.DEFAULT));
                    passcodeView.setPasscodeLength(PASS.length())
                            .setLocalPasscode(PASS)
                            .setListener(new PasscodeView.PasscodeViewListener() {
                                @Override
                                public void onFail() {
                                    new ToastMsg(Login.this).toastIconError(getString(R.string.wrong_password));
                                }

                                @Override
                                public void onSuccess(String number) {
                                    Intent intent_passcode = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent_passcode);
                                }

                    });

                } else {
                    new ToastMsg(Login.this).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<Auth> call, @NotNull Throwable t) {
                new ToastMsg(Login.this).toastIconError(getString(R.string.error_toast));
            }
        });

    }
}