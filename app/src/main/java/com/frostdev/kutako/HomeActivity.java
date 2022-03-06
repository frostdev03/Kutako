package com.frostdev.kutako;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.frostdev.kutako.Adapter.PostAdapter;
import com.frostdev.kutako.Model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private PostAdapter postAdapter;
    private List< Post > postList;

    private CircleImageView navHeaderImage;
    private TextView navHeadreEmail, navHeaderName;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.rv);
        progressBar = findViewById(R.id.progres_circular);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        drawerLayout = findViewById(R.id.drawer_layout);
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kutako");

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AskQuestionActivity.class);
                startActivity(intent);
            }
        });

        navHeadreEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        navHeaderName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        navHeaderImage = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_pict);
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("TAG","data snapshot : "+snapshot);
                navHeaderName.setText(snapshot.child("username").getValue().toString());
                navHeadreEmail.setText(snapshot.child("email").getValue().toString());

                Glide.with(HomeActivity.this).load(snapshot.child("profileImageUrl").getValue()).into(navHeaderImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(HomeActivity.this, postList);
        recyclerView.setAdapter(postAdapter);

        readQuestionsPost();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void readQuestionsPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_tech:
                Intent intent = new Intent(HomeActivity.this, CategorySelectedActivity.class);
                intent.putExtra("title", "Programming");
                startActivity(intent);
                break;

            case R.id.nav_edu:
                Intent intents = new Intent(HomeActivity.this, CategorySelectedActivity.class);
                intents.putExtra("title", "Technology");
                startActivity(intents);
                break;

            case R.id.nav_covid:
                Intent intentf = new Intent(HomeActivity.this, CategorySelectedActivity.class);
                intentf.putExtra("title", "Education");
                startActivity(intentf);
                break;

            case R.id.nav_free:
                Intent intentfre = new Intent(HomeActivity.this, CategorySelectedActivity.class);
                intentfre.putExtra("title", "Random Question");
                startActivity(intentfre);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intentOut = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intentOut);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}