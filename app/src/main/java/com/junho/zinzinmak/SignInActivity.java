
package com.junho.zinzinmak;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.junho.zinzinmak.models.User;
import com.junho.zinzinmak.R;

import java.util.Objects;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenenr;


    // 이메일 입력창
    private EditText mEmailField;
    // 비밀번호 입력창
    private EditText mPasswordField;
    // 로그인 버튼
    private Button mSignInButton;
    // 회원가입 버튼
    private Button mSignUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // OnCreate로 만들어지자마자 이렇게 뷰들을 일단 가져와야지.
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mSignInButton = findViewById(R.id.buttonSignIn);
        mSignUpButton = findViewById(R.id.buttonSignUp);
        setProgressBar(R.id.progressBar);

        // 클릭을 할 시에 하는 행동(로그인, 회원가입 버튼)
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);

        mAuthListenenr = new FirebaseAuth.AuthStateListener() {
            FirebaseUser user = mAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    // 시작을 했을 때에 유저가 널이 아니다? 유저가 이미 있다면 그러면 사인인을 했다는 거지. 그러면 메인으로 넘어가라고
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish(); // 현재의 액티비티를 중지 시키는 거고.
                    Toast.makeText(SignInActivity.this, "user id: " + mAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListenenr); // listener connect
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListenenr != null){
            mAuth.removeAuthStateListener(mAuthListenenr);
        }
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressBar();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 마친 경우 task 가져오라구
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressBar();

                        if (task.isSuccessful()) {
                            onAuthSuccess(Objects.requireNonNull(task.getResult().getUser()));
                        } else {
                            Toast.makeText(SignInActivity.this, "회원가입 실패",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        //startActivity(new Intent(SignInActivity.this, MainActivity.class));
        //finish();
        Toast.makeText(SignInActivity.this, "로그인",Toast.LENGTH_LONG).show();
    }

    private String usernameFromEmail(String email) {
        // 유저 이메일 볼 때에 골뱅이 부분으로 끊자
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        // 입력 안하고 개기면 이 부분은 꼭 필요하다고 말을 해주라고.
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        // 입력 안하고 개기면 이 부분은 꼭 필요하다고 말을 해주라고.
        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {
            String email = mEmailField.getText().toString();
            String password = mPasswordField.getText().toString();
            loginUser(email, password);
            //signIn();
        } else if (i == R.id.buttonSignUp) {
            signUp();
        }
    }
    private  void loginUser(final String email, final String password){
        showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignInActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                } else{
                    // fail
                    Toast.makeText(SignInActivity.this,"이메일 혹은 비밀번호를 다시 확인해 주세", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
