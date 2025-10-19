package com.example.carshering.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.MainActivity;
import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.R;
import com.example.carshering.databinding.ActivityLoginBinding;
import com.example.carshering.model.User;
import com.example.carshering.api.ApiClient;
import com.example.carshering.ui.register.RegisterStep1Activity;
import com.example.carshering.utils.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthCredential;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // ID-токен FireBase в .json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso); // экран Google-аккаунт

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = binding.etLoginEmail.getText().toString().trim();
                String pass = binding.etLoginPassword.getText().toString().trim();
                binding.btnLogin.setEnabled(!email.isEmpty() && !pass.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.etLoginEmail.addTextChangedListener(watcher);
        binding.etLoginPassword.addTextChangedListener(watcher);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etLoginEmail.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();
            User user = new User(email, password);

            ApiClient.getApiService().login(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response
                ) {
                    if (response.isSuccessful()) {

                        getSharedPreferences("app_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("user_email", email)
                                .apply();

                        Toast.makeText(LoginActivity.this,
                                R.string.login_success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(
                                LoginActivity.this,
                                MainActivity.class
                        ));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                R.string.login_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoginActivity.this,
                            R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        binding.btnGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this,
                        RegisterStep1Activity.class))
        );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, R.string.google_signin_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        String email = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;
                        if (email != null) {
                            getSharedPreferences("app_prefs", MODE_PRIVATE)
                                    .edit()
                                    .putString("user_email", email)
                                    .apply();
                        }
                        Toast.makeText(this,
                                R.string.login_success,
                                Toast.LENGTH_SHORT
                        ).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this,
                                R.string.google_auth_error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
