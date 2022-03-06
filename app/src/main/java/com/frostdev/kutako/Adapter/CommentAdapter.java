package com.frostdev.kutako.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.frostdev.kutako.Model.Comment;
import com.frostdev.kutako.Model.User;
import com.frostdev.kutako.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder>{

    private Context mContext;
    private List< Comment > mCommentList;
    private String postId;
    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List< Comment > mCommentList, String postId) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_layout, parent, false);
        return new CommentAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mCommentList.get(position);

        holder.commenterComment.setText(comment.getComment());
        holder.commentDate.setText(comment.getDate());

        getUserInformation(holder.commenterProfilePict, holder.commmentorUser, comment.getPublisher());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public CircleImageView commenterProfilePict;
        public TextView commenterComment, commmentorUser, commentDate;


        public viewHolder(@NonNull View itemView) {
            super(itemView);

            commenterProfilePict = itemView.findViewById(R.id.commenter_profile_pict);
            commenterComment = itemView.findViewById(R.id.commenter_comment);
            commmentorUser = itemView.findViewById(R.id.commenter_name);
            commentDate = itemView.findViewById(R.id.commrnt_date);
        }
    }

    private void getUserInformation(CircleImageView profilePict, TextView username, String publisherId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileImageUrl()).into(profilePict);
                username.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
