package com.frostdev.kutako;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class AskQuestionActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText edtQuestion;
    private ImageView questtionImage;
    private TextView btnCancel, btnPost;

    private String askedByName = "";
    private DatabaseReference askedByRef;
    private ProgressDialog loader;
    private String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;
    private Uri imageViewUri;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onLineUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ask_question);

        spinner = findViewById(R.id.spinner);
        edtQuestion = findViewById(R.id.question_text);
        questtionImage = findViewById(R.id.question_image);
        btnCancel = findViewById(R.id.btn_cancel);
        btnPost = findViewById(R.id.btn_post_question);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onLineUserId = mUser.getUid();

        askedByRef = FirebaseDatabase.getInstance().getReference("users").child(onLineUserId);
        askedByRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                askedByName = snapshot.child("fullname").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("questions");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Topics));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView< ? > parent, View view, int position, long id) {
                if (spinner.getSelectedItem().equals("select topic")){
                }
            }

            @Override
            public void onNothingSelected(AdapterView< ? > parent) {

            }
        });

        questtionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performValidation();
            }
        });
    }

    String getQuestionText(){
        return edtQuestion.getText().toString().trim();
    }

    String getTopic(){
        return spinner.getSelectedItem().toString();
    }

    String mData = DateFormat.getInstance().format(new Date());
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("questions posts");

    private void performValidation() {
        if (getQuestionText().isEmpty()){
            edtQuestion.setError("Question can not be empty");
        } else if (getTopic().equals("select topic")){
            Toast.makeText(this, "select a topic", Toast.LENGTH_SHORT).show();
        } else if (!getQuestionText().isEmpty() && !getTopic().equals("") && imageViewUri == null){
            uploadQuestionWithoutImage();
        } else if (!getQuestionText().isEmpty() && !getTopic().equals("") && imageViewUri != null){
            uploadQuestionWithImage();
        }
    }

    private void startLoader(){
        loader.setMessage("posting your question");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadQuestionWithoutImage(){
        startLoader();
        String postId = ref.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", postId);
        hashMap.put("question", getQuestionText());
        hashMap.put("publisher", onLineUserId);
        hashMap.put("topic", getTopic());
        hashMap.put("askedby", askedByName);
        hashMap.put("data", mData);

        ref.child(postId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener< Void >() {
            @Override
            public void onComplete(@NonNull Task< Void > task) {
                if (task.isSuccessful()){
                    Toast.makeText(AskQuestionActivity.this, "Question posted", Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                    startActivity(new Intent(AskQuestionActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(AskQuestionActivity.this, "Could not upluad image "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        });
    }

    private void uploadQuestionWithImage(){
        startLoader();
        final StorageReference fileReff;
        fileReff = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageViewUri));
        uploadTask = fileReff.putFile(imageViewUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isComplete()){
                    throw task.getException();
                }
                return fileReff.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Uri downloadUri = (Uri) task.getResult();
                    myUrl = downloadUri.toString();
                    String postId = ref.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("postId", postId);
                    hashMap.put("question", getQuestionText());
                    hashMap.put("publisher", onLineUserId);
                    hashMap.put("topic", getTopic());
                    hashMap.put("askedby", askedByName);
                    hashMap.put("questionimage", myUrl);
                    hashMap.put("data", mData);

                    ref.child(postId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener< Void >() {
                        @Override
                        public void onComplete(@NonNull Task< Void > task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AskQuestionActivity.this, "Question posted", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                                startActivity(new Intent(AskQuestionActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(AskQuestionActivity.this, "Could not upluad image "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AskQuestionActivity.this, "Failed uplad the question", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageViewUri = data.getData();
            questtionImage.setImageURI(imageViewUri);
        }
    }
}