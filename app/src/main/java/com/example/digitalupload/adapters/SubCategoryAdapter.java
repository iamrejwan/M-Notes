package com.example.digitalupload.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalupload.R;
import com.example.digitalupload.SubCategoryActivity;
import com.example.digitalupload.UsersActivity;
import com.example.digitalupload.models.CategoryModel;
import com.example.digitalupload.utils.ItemAnimation;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
    private Context context;


    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;
    private String category_id;
    private List<CategoryModel> cardItemList;

    public SubCategoryAdapter(Context ctx, List<CategoryModel> items, String category_id) {
        cardItemList = items;
        context = ctx;
        this.category_id = category_id;
    }

    @NonNull
    @Override
    public SubCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.ViewHolder holder, int position) {
        //----fetch array------------

        CategoryModel obj = cardItemList.get(position);

        holder.cardView.requestFocus();
        holder.name.setText(obj.getName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, UsersActivity.class);
                intent.putExtra("category_id", category_id);
                intent.putExtra("sub_category_id", obj.getId());
                context.startActivity(intent);

            }
        });


        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return cardItemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });


        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}