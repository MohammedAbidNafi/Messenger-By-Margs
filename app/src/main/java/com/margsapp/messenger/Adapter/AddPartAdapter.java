package com.margsapp.messenger.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Fragments.AddParticipantsFragment;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.SplashActivity;
import com.margsapp.messenger.StartActivity;
import com.margsapp.messenger.groupclass.AddParticipants;
import com.victor.loading.rotate.RotateLoading;

import java.util.EventListener;
import java.util.HashMap;
import java.util.List;

public class AddPartAdapter extends RecyclerView.Adapter<AddPartAdapter.ViewHolder> {

    private final Context mContext;
    private  List<User> mUsers;

    private String GroupName;

    EventListener listener;



    public interface EventListener {
        void AddParticipant(String id, String username, RotateLoading rotateLoading, Context mContext,ImageView remove);
    }

    public void addEventListener(EventListener listener){
        this.listener = listener;
    }

    public void removeEventListener(){
        listener = null;
    }


    public AddPartAdapter(Context mContext, List<User> mUsers,String GroupName) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.GroupName = GroupName;

    }



    @NonNull
    @Override
    public AddPartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.add_part_item, parent, false);
        return new AddPartAdapter.ViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull AddPartAdapter.ViewHolder holder, int position) {
        final User user = mUsers.get(position);


        holder.username.setText(user.getUsername());
        holder.dt.setText(user.getDt());

        if (user.getImageUrl().equals("default")) {
            holder.dp.setImageResource(R.drawable.user);
        } else {
            Glide.with(mContext).load(user.getImageUrl()).into(holder.dp);
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(GroupName).child("members");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Group group = snapshot.getValue(Group.class);
                assert group != null;
                if(user.getId().equals(group.getId())){
                    holder.addpart_btn.setVisibility(View.GONE);
                    holder.remove.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.addpart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Variable = user.getId();
                String Variable2 = user.getUsername();
                holder.rotateLoading.setVisibility(View.VISIBLE);
                holder.addpart_btn.setVisibility(View.GONE);
                holder.rotateLoading.start();
                //Send call that method and send the variablethat variable to Fragment
                new Handler().postDelayed(new Runnable() {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run() {
                        listener.AddParticipant(Variable, Variable2, holder.rotateLoading, mContext,holder.remove);
                    }
                },  1100); // wait for 5 seconds
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = user.getId();
                String name = user.getUsername();
                remove(id,name);

                holder.remove.setVisibility(View.GONE);
                holder.addpart_btn.setVisibility(View.VISIBLE);
                /*
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddParticipants.class);
                dialog.setMessage("Are you sure you want to remove this user?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dont do anything
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

                 */
            }
        });

    }

    private void remove(String id, String name) {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Grouplist").child(id).child(GroupName);
        databaseReference1.removeValue();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(GroupName).child("members").child(id);
        databaseReference.removeValue();

        Toast.makeText(mContext, name + " has been removed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView dp;
        private ImageView unread, remove;


        public TextView dt;
        private final TextView username;
        RotateLoading rotateLoading;

        AppCompatButton addpart_btn;


        public ViewHolder(View view){
            super(view);

            remove = itemView.findViewById(R.id.remove);
            rotateLoading = itemView.findViewById(R.id.loading);
            dp = itemView.findViewById(R.id.profile_image);
            dt = itemView.findViewById(R.id.dt);
            unread = itemView.findViewById(R.id.unread);
            username = itemView.findViewById(R.id.username);
            addpart_btn = itemView.findViewById(R.id.addpart);

        }
    }
}
