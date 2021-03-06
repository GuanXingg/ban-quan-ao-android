package com.example.banhangonline.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banhangonline.R;
import com.example.banhangonline.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    Button signUp;
    EditText name , email , password ,telephone ,address;
    TextView signIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        signUp = findViewById(R.id.reg_btn);
        name =findViewById(R.id.name);
        email = findViewById(R.id.email_reg);
        address = findViewById(R.id.address_reg);
        telephone = findViewById(R.id.tele_reg);
        password = findViewById(R.id.password_reg);
        signIn = findViewById(R.id.sign_in);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLogin = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createUser() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userAddress = address.getText().toString();
        String userTelephone = telephone.getText().toString();
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this,"T??n c??n tr???ng",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(this,"Email c??n  tr???ng",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"M???t kh???u c??n  tr???ng",Toast.LENGTH_SHORT).show();
            return;
        }
        if(userPassword.length() < 6 ){
            Toast.makeText(this,"M???t kh???u c?? ??t nh???t 6 k?? t???",Toast.LENGTH_SHORT).show();
            return;

        }
        if (TextUtils.isEmpty(userAddress)){
            Toast.makeText(this,"?????a ch??? c??n  tr???ng",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userTelephone)){
            Toast.makeText(this,"S??? ??i???n tho???i c??n tr???ng",Toast.LENGTH_SHORT).show();
            return;
        }
        // t???o m???i user
        auth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //,userAddress,userTelephone
                            UserModel userModel = new UserModel(userName,userEmail,userAddress,userTelephone);
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(userModel);
                            sendEmailVerification();

//                            Toast.makeText(getApplicationContext(),"????ng k?? th??nh c??ng",Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"????ng k?? th???t b???i",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    protected void sendEmailVerification(){
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Tin nh???n ???? g???i ?????n email vui l??ng x??c th???c v?? ????ng nh???p l???i", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    finish();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "G???i kh??ng th??nh c??ng", Toast.LENGTH_SHORT).show();

        }
    }
}