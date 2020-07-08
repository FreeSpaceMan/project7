package com.example.projektas7;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<UserMessages> usersMessages = new ArrayList<>();
    private Context context;

    public UserAdapter(Context context, ArrayList<UserMessages> usersMessages) {
        this.usersMessages = usersMessages;
        this.context = context;
    }


    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_messaging,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder viewHolder, int i) {
        viewHolder.user_name.setText(usersMessages.get(i).getUsername());
        viewHolder.user_message.setText(usersMessages.get(i).getMessage());
        viewHolder.user_image.setImageResource(R.drawable.king_card);

//        Picasso.get().load(carsModels.get(i).getImage()).into(viewHolder.car_image);
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return usersMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_image;
        private TextView user_name, user_message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image=(ImageView)itemView.findViewById(R.id.user_image);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            user_message=(TextView)itemView.findViewById(R.id.user_message);
        }
    }
}