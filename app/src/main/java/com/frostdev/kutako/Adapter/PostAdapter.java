package com.frostdev.kutako.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.frostdev.kutako.CommentsActivity;
import com.frostdev.kutako.Model.Post;
import com.frostdev.kutako.Model.User;
import com.frostdev.kutako.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{

    public Context mContext;
    public List< Post > onPostList;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List< Post > onPostList) {
        this.mContext = mContext;
        this.onPostList = onPostList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.questions_retrieved_layout, parent, false);
        return new PostAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = onPostList.get(position);
        if (post.getQuestionimage() == null){
            holder.questionImage.setVisibility(View.GONE);
        }

        holder.questionImage.setVisibility(View.VISIBLE);
        Glide.with(mContext).load(post.getQuestionimage()).into(holder.questionImage);
        holder.expandableTextView.setText(post.getQuestion());
        holder.topicTextview.setText(post.getTopic());
        holder.askedOnTextview.setText(post.getData());

        publisherInformation(holder.publisherProfilePhoto, holder.askedByTextview, post.getPublisher());
        isLiked(post.getPostId(), holder.like);
        isDisliked(post.getPostId(), holder.dislike);

        getLikes(holder.likes, post.getPostId());
        getDisikes(holder.dislikes, post.getPostId());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like") && holder.dislike.getTag().equals("dislike")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                } else if (holder.like.getTag().equals("like") && holder.dislike.getTag().equals("disliked")){
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                } else if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("liked")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.coment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("publisher", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.coments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("publisher", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.inflate(R.menu.post_menu);

                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return onPostList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public CircleImageView publisherProfilePhoto;
        public TextView askedByTextview, topicTextview, askedOnTextview, likes, dislikes, coments, saved;
        public ImageView more, questionImage, like, dislike, coment, save;
        public ExpandableTextView expandableTextView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            publisherProfilePhoto = itemView.findViewById(R.id.pulisher_profile_image);
            askedByTextview = itemView.findViewById(R.id.asked_by_textview);
            likes = itemView.findViewById(R.id.txt_like);
            dislikes = itemView.findViewById(R.id.txt_dislike);
            coments = itemView.findViewById(R.id.txt_comment);
            more = itemView.findViewById(R.id.more);
            questionImage = itemView.findViewById(R.id.question_image);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
            coment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            topicTextview = itemView.findViewById(R.id.topic_txt);
            askedOnTextview = itemView.findViewById(R.id.asked_on_textview);
            expandableTextView = itemView.findViewById(R.id.expand_text_view);
            saved = itemView.findViewById(R.id.txt_save);
        }
    }

    private void publisherInformation(CircleImageView publisherImage, TextView askedBy, String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    Glide.with(mContext).load(user.getProfileImageUrl()).into(publisherImage);
                    askedBy.setText(user.getFullname());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isLiked(String postId, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isDisliked(String postId, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("dislikes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_disliked);
                    imageView.setTag("disliked");
                } else {
                    imageView.setImageResource(R.drawable.ic_dislike);
                    imageView.setTag("dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes(TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numberOfLikes = snapshot.getChildrenCount();
                int NOL = (int) numberOfLikes;
                if (NOL > 1){
                    likes.setText(snapshot.getChildrenCount()+ " like");
                } else if (NOL == 0){
                    likes.setText( "0 likes");
                } else {
                    likes.setText(snapshot.getChildrenCount()+ " like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDisikes(TextView dislikes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dislikes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numberOfDislikes = snapshot.getChildrenCount();
                int NOD = (int) numberOfDislikes;
                if (NOD > 1){
                    dislikes.setText(snapshot.getChildrenCount()+ " dislike");
                } else if (NOD == 0){
                    dislikes.setText( "0 dislikes");
                } else {
                    dislikes.setText(snapshot.getChildrenCount()+ " dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
