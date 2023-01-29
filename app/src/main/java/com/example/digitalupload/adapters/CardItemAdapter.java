package com.example.digitalupload.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalupload.R;
import com.example.digitalupload.SubCategoryActivity;
import com.example.digitalupload.UserDetailsActivity;
import com.example.digitalupload.models.CategoryModel;
import com.example.digitalupload.models.UserModel;
import com.example.digitalupload.utils.ItemAnimation;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardItemAdapter extends RecyclerView.Adapter<CardItemAdapter.ViewHolder> {

    private Context context;


    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;
    private int c;
    private List<UserModel> cardItemList;

    public CardItemAdapter(Context context, List<UserModel> cardItemList) {
        this.context = context;
        this.cardItemList = cardItemList;
    }

    @NonNull
    @Override
    public CardItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_detail, parent,
                false);

        return new CardItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardItemAdapter.ViewHolder holder, int position) {

        UserModel obj = cardItemList.get(position);
        holder.name.setText(context.getString(R.string.name_text)+": "+obj.getName());
        holder.phone.setText(context.getString(R.string.phone_text)+": "+obj.getPhone());
        holder.rank.setText(context.getString(R.string.rank)+": "+obj.getRank());
        Picasso.get()
                .load(obj.getImageURL())
                .placeholder(R.drawable.ic_user)
                .into(holder.image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, UserDetailsActivity.class);
                intent.putExtra("uid", obj.getId());
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
        TextView name, phone, rank;
        CardView cardView;
        CircleImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            rank = itemView.findViewById(R.id.rank);
            cardView = itemView.findViewById(R.id.card_view);
            image = itemView.findViewById(R.id.image);

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
