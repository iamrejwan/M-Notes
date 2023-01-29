package com.example.digitalupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalupload.adapters.CardItemAdapter;
import com.example.digitalupload.models.CommonModel;
import com.example.digitalupload.models.UserModel;
import com.example.digitalupload.network.RetrofitClient;
import com.example.digitalupload.network.api.UserApi;
import com.example.digitalupload.utils.AppConfig;
import com.example.digitalupload.utils.FileUtil;
import com.example.digitalupload.utils.ToastMsg;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserDetailsActivity extends AppCompatActivity {

    private String uid;
    private TextView name;
    private TextView phone;
    private TextView rank;
    private TextView address;
    private TextView category;
    private TextView sub_category;
    private Button edit, update;
    private ImageView editProfilePicture, phoneIcon;
    private CircleImageView userIv;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri imageUri;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_deatils);

        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(50);


        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        rank = findViewById(R.id.rank);
        address = findViewById(R.id.addr);
        category = findViewById(R.id.category);
        sub_category = findViewById(R.id.category_sub);
        update = findViewById(R.id.update);
        edit = findViewById(R.id.edit);
        userIv = findViewById(R.id.user_iv);
        editProfilePicture  = findViewById(R.id.pro_pic_edit_image_view);
        phoneIcon = findViewById(R.id.phone_icon);

        uid = getIntent().getStringExtra("uid");


        getProfile();


        phoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone.getText()));
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                phone.setEnabled(true);
                rank.setEnabled(true);
                address.setEnabled(true);
                update.setVisibility(View.VISIBLE);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(UserDetailsActivity.this, "Name cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                } else if (phone.getText().toString().equals("")) {
                    Toast.makeText(UserDetailsActivity.this, "Phone Number cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                } else if (rank.getText().toString().equals("")) {
                    Toast.makeText(UserDetailsActivity.this, "Rank cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                progressBar.setProgress(50);

                String fullName = name.getText().toString();
                String phoneNumber = phone.getText().toString();
                String userRank = rank.getText().toString();
                String userAddress = address.getText().toString();
                String userCategory = category.getText().toString();
                String userSubCategory = sub_category.getText().toString();


                updateProfile( uid, fullName, phoneNumber, userRank, userAddress, userCategory, userSubCategory);

            }
        });

    }

    private void getProfile(){
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserApi api = retrofit.create(UserApi.class);
        Call<UserModel> call = api.getUserById(uid);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.code() == 200) {
                    progressBar.setVisibility(View.GONE);
                    UserModel userModel = response.body();
                    name.setText(userModel.getName());
                    phone.setText(userModel.getPhone());
                    rank.setText(userModel.getRank());
                    address.setText(userModel.getAddress());
                    category.setText(userModel.getCategory_name());
                    sub_category.setText(userModel.getSubcategory_name());
                    Picasso.get()
                            .load(AppConfig.API_SERVER_URL + userModel.getImageURL())
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(userIv);

                } else {
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(UserDetailsActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                new ToastMsg(UserDetailsActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
    }

    private void updateProfile(String userId, String nameString, String phoneString, String rankString, String addressString, String categoryString, String subCategoryString) {
        File file;
        RequestBody requestFile;
        MultipartBody.Part multipartBody = null;
        try {
            if (imageUri != null) {
                file = FileUtil.from(UserDetailsActivity.this, imageUri);
                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),
                        file);

                multipartBody = MultipartBody.Part.createFormData("image_url",
                        file.getName(), requestFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), nameString);
        RequestBody number = RequestBody.create(MediaType.parse("text/plain"), phoneString);
        RequestBody rank = RequestBody.create(MediaType.parse("text/plain"), rankString);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), addressString);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserApi api = retrofit.create(UserApi.class);
        Call<UserModel> call = api.updateUser(userId, name, number, rank, multipartBody, address );
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call,@NotNull  Response<UserModel> response) {
                if (response.code() == 200) {
                    progressBar.setVisibility(View.GONE);
                    assert response.body() != null;
                    getUpdatedData(response.body());

                } else {
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(UserDetailsActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                new ToastMsg(UserDetailsActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
    }

    private void getUpdatedData(UserModel body) {
        name.setEnabled(false);
        phone.setEnabled(false);
        rank.setEnabled(false);
        address.setEnabled(false);
        update.setVisibility(View.GONE);

        UserModel userModel = body;
        name.setText(userModel.getName());
        phone.setText(userModel.getPhone());
        rank.setText(userModel.getRank());
        address.setText(userModel.getAddress());
        category.setText(userModel.getCategory_name());
        sub_category.setText(userModel.getSubcategory_name());
        Picasso.get()
                .load(AppConfig.API_SERVER_URL + userModel.getImageURL())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(userIv);
    }


    @Override
    protected void onStart() {
        super.onStart();

        editProfilePicture.setOnClickListener(v -> openGallery());

    }

    private void openGallery() {
        ImagePicker.with(UserDetailsActivity.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(500)			//Final image size will be less than 500 KB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            Uri selectedImage = data.getData();
            userIv.setImageURI(selectedImage);
            imageUri = selectedImage;
        }else if (resultCode == ImagePicker.RESULT_ERROR) {
            new ToastMsg(UserDetailsActivity.this).toastIconError(ImagePicker.Companion.getError(data));
        } else {
            new ToastMsg(UserDetailsActivity.this).toastIconError(getString(R.string.task_cancelled));
        }
    }



}