package com.example.mohamedabdelaziz.marketstore;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private String user_profile_url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signin_btn).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.loading, Toast.LENGTH_LONG).show();
            } else {
                findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            findViewById(R.id.signin_btn).setVisibility(View.INVISIBLE);
                            findViewById(R.id.email_login_form).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.name)).setText(user.getDisplayName());
                            ((TextView) findViewById(R.id.phone)).setText(user.getPhoneNumber());
                            ((TextView) findViewById(R.id.email)).setText(user.getEmail());
                            ((TextView) findViewById(R.id.email)).setEnabled(false);
                            try {
                                Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(((ImageView) findViewById(R.id.profile_image)));
                                user_profile_url = "" + user.getPhotoUrl();
                            } catch (Exception e) {
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.auth_fail,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.google_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signin_btn) {
            signIn();
        }
    }

    public void register_user(View view) {
        EditText name = (EditText) findViewById(R.id.name);
        EditText phone = (EditText) findViewById(R.id.phone);
        EditText email = (EditText) findViewById(R.id.email);
        EditText address = (EditText) findViewById(R.id.address);
        EditText work = (EditText) findViewById(R.id.work);
        if (name.getText().toString().isEmpty()) {
            name.setError(getString(R.string.name_rq));
            return;
        }
        if (address.getText().toString().isEmpty()) {
            address.setError(getString(R.string.address_rq));
            return;
        }
        if (work.getText().toString().isEmpty()) {
            work.setError(getString(R.string.work_req));
            return;
        }
        String phone_number = phone.getText().toString().replaceAll("\\s", "").replace("+2", "").trim();
        if (phone_number.isEmpty() || phone_number.length() != 11 || !phone_number.startsWith("01")) {
            phone.setError(getString(R.string.phone_error));
            return;
        }
        if (register_user(new UserDataTypes(name.getText().toString(), email.getText().toString(), phone_number, work.getText().toString(), address.getText().toString(), user_profile_url))) {
            SharedPreferences sharedPreferences = getSharedPreferences("Logged_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("logged", true);
            editor.putString("name", name.getText().toString());
            editor.putString("phone", phone_number);
            editor.putString("email", email.getText().toString());
            editor.putString("url", user_profile_url);
            editor.putString("work", work.getText().toString());
            editor.putString("address", address.getText().toString());
            editor.commit();
            startActivity(new Intent(getApplicationContext(), TabActivity.class));
            finish();
        }
    }

    private boolean register_user(UserDataTypes user) {
        try {
            FirebaseDatabase fireDatabase;
            fireDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = fireDatabase.getReference("users");
            myRef.child(user.getEmail().replace("@gmail.com", "").trim().toString()).setValue(user);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}