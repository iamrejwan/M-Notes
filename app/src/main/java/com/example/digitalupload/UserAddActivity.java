package com.example.digitalupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.digitalupload.adapters.CategoryAdapter;
import com.example.digitalupload.models.CategoryModel;
import com.example.digitalupload.models.CommonModel;
import com.example.digitalupload.models.UserModel;
import com.example.digitalupload.network.RetrofitClient;
import com.example.digitalupload.network.api.CategoryApi;
import com.example.digitalupload.network.api.SubCategoryApi;
import com.example.digitalupload.network.api.UserApi;
import com.example.digitalupload.utils.FileUtil;
import com.example.digitalupload.utils.ToastMsg;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Address;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserAddActivity extends AppCompatActivity {

    private TextInputEditText nameEditView, phoneEditView, rankEditView, addressEditView;
    private AutoCompleteTextView categoryTextView, subCategoryTextView;
    private Button submit;
    private ImageView editProfilePicture;
    private CircleImageView userIv;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri imageUri;
    private ArrayList<String> category_item = new ArrayList<String>();
    private HashMap<String ,String> categoryItem = new HashMap<String,String>(); // To store key value
    private HashMap<String ,String> subcategoryItem = new HashMap<String,String>(); // To store key value
    private ProgressBar progressBar;
    private TextInputLayout categorySubLyt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_form);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);

        nameEditView = findViewById(R.id.name);
        phoneEditView = findViewById(R.id.phone);
        rankEditView = findViewById(R.id.rank);
        addressEditView = findViewById(R.id.addr);
        categoryTextView = findViewById(R.id.category);
        subCategoryTextView = findViewById(R.id.category_sub);
        categorySubLyt = findViewById(R.id.category_sub_lyt);
        submit = findViewById(R.id.submit);
        userIv = findViewById(R.id.user_iv);
        editProfilePicture  = findViewById(R.id.pro_pic_edit_image_view);
        if (category_item.size()==0){
            getCategoryList();
        }

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        category_item);

        categoryTextView.setAdapter(categoryAdapter);
        categoryTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                getSubCategoryItem(categoryItem.get(adapterView.getItemAtPosition(position).toString()));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditView.getText().toString().equals("")) {
                    Toast.makeText(UserAddActivity.this, "Name cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                } else if (phoneEditView.getText().toString().equals("")) {
                    Toast.makeText(UserAddActivity.this, "Phone Number cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                } else if (rankEditView.getText().toString().equals("")) {
                    Toast.makeText(UserAddActivity.this, "Rank cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                } else if (addressEditView.getText().toString().equals("")) {
                    Toast.makeText(UserAddActivity.this, "Address cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                progressBar.setProgress(50);

                String fullName = nameEditView.getText().toString();
                String phoneNumber = phoneEditView.getText().toString();
                String userRank = rankEditView.getText().toString();
                String userAddress = addressEditView.getText().toString();
                String userCategory = categoryItem.get(categoryTextView.getText().toString());
                String userSubCategory = subcategoryItem.get(subCategoryTextView.getText().toString());


                addProfile(fullName, phoneNumber, userRank, userAddress, userCategory, userSubCategory);

            }
        });


    }

    private void getSubCategoryItem(String category_id) {
        ArrayList<String> sub_category_item = new ArrayList<>();
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SubCategoryApi api = retrofit.create(SubCategoryApi.class);
        Call<CommonModel> call = api.getSubCategoryById(category_id);
        call.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                if (response.code() == 200) {

                    CommonModel commonModel = response.body();

                    for (int i = 0; i < commonModel.getCategoryModelsList().size(); i++) {
                        sub_category_item.add(commonModel.getCategoryModelsList().get(i).getName());
                        subcategoryItem.put(commonModel.getCategoryModelsList().get(i).getName(), commonModel.getCategoryModelsList().get(i).getId());
                        updateSubcategory(sub_category_item);
                    }

                } else {

                    new ToastMsg(UserAddActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                new ToastMsg(UserAddActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
    }

    private void updateSubcategory(ArrayList<String> sub_category_item) {
        ArrayAdapter<String> subCategoryAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        sub_category_item);

        subCategoryTextView.setAdapter(subCategoryAdapter);
        categorySubLyt.setVisibility(View.VISIBLE);
    }

    private void getCategoryList(){
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        CategoryApi api = retrofit.create(CategoryApi.class);
        Call<CommonModel> call = api.getHomeCategory();
        call.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                if (response.code() == 200) {

                    CommonModel commonModel = response.body();

                    for (int i = 0; i < commonModel.getCategoryModelsList().size(); i++) {
                        category_item.add(commonModel.getCategoryModelsList().get(i).getName());
                        categoryItem.put(commonModel.getCategoryModelsList().get(i).getName(), commonModel.getCategoryModelsList().get(i).getId());
                    }

                } else {
                    new ToastMsg(UserAddActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }
            @Override
            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                new ToastMsg(UserAddActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
    }

    private void addProfile(String fullName, String phoneNumber, String userRank, String userAddress, String userCategory, String userSubCategory) {
        File file;
        RequestBody requestFile;
        MultipartBody.Part multipartBody = null;
        try {
            if (imageUri != null) {
                file = FileUtil.from(UserAddActivity.this, imageUri);
                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),
                        file);

                multipartBody = MultipartBody.Part.createFormData("image_url",
                        file.getName(), requestFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), fullName);
        RequestBody number = RequestBody.create(MediaType.parse("text/plain"), phoneNumber);
        RequestBody rank = RequestBody.create(MediaType.parse("text/plain"), userRank);
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), userAddress);
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"), userCategory);
        RequestBody subCategory = RequestBody.create(MediaType.parse("text/plain"), userSubCategory);


        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserApi api = retrofit.create(UserApi.class);
        Call<UserModel> call = api.addUser(name, number, rank, address, category, multipartBody, subCategory );
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull  retrofit2.Response<UserModel> response) {
                if (response.code() == 201) {
                    nameEditView.setText("");
                    phoneEditView.setText("");
                    rankEditView.setText("");
                    addressEditView.setText("");
                    categoryTextView.setText("");
                    subCategoryTextView.setText("");
                    userIv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_user));
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(UserAddActivity.this).toastIconSuccess(getString(R.string.success_toast));

                } else {
                    progressBar.setVisibility(View.GONE);
                    new ToastMsg(UserAddActivity.this).toastIconError(getString(R.string.error_toast));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                new ToastMsg(UserAddActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        editProfilePicture.setOnClickListener(v -> openGallery());

    }

    private void openGallery() {
        ImagePicker.with(UserAddActivity.this)
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
            new ToastMsg(UserAddActivity.this).toastIconError(ImagePicker.Companion.getError(data));
        } else {
            new ToastMsg(UserAddActivity.this).toastIconError(getString(R.string.task_cancelled));
        }
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