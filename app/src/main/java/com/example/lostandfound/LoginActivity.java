package com.example.lostandfound;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioUser, radioAdmin;
    private LinearLayout userFields, adminFields;
    private TextInputEditText etName, etEmail, etPhone, etAdminUsername, etAdminPassword;
    private Button btnLogin;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initializeViews();

        // Check login status
        checkLoginStatus();

        // Setup radio group listener
        setupRadioGroup();

        // Setup text change listeners
        setupTextWatchers();

        // Set login button click listener
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void initializeViews() {
        radioGroup = findViewById(R.id.radioGroup);
        radioUser = findViewById(R.id.radioUser);
        radioAdmin = findViewById(R.id.radioAdmin);
        userFields = findViewById(R.id.userFields);
        adminFields = findViewById(R.id.adminFields);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAdminUsername = findViewById(R.id.etAdminUsername);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnLogin = findViewById(R.id.btnLogin);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LostAndFoundPrefs", MODE_PRIVATE);

        // Initially disable login button
        btnLogin.setEnabled(false);
    }

    private void checkLoginStatus() {
        if (sharedPreferences.getLong("userId", -1) != -1) {
            boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            startActivity(new Intent(this,
                    isAdmin ? AdminDashboardActivity.class : HomeActivity.class));
            finish();
        }
    }

    private void setupRadioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioUser) {
                userFields.setVisibility(View.VISIBLE);
                adminFields.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioAdmin) {
                userFields.setVisibility(View.GONE);
                adminFields.setVisibility(View.VISIBLE);
            }
            validateForm();
        });
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                validateForm();
            }
        };

        etName.addTextChangedListener(textWatcher);
        etEmail.addTextChangedListener(textWatcher);
        etPhone.addTextChangedListener(textWatcher);
        etAdminUsername.addTextChangedListener(textWatcher);
        etAdminPassword.addTextChangedListener(textWatcher);
    }

    private void validateForm() {
        boolean isValid;

        if (radioUser.isChecked()) {
            // User registration validation
            isValid = !etName.getText().toString().trim().isEmpty() &&
                    isValidEmail(etEmail.getText().toString().trim()) &&
                    isValidPhone(etPhone.getText().toString().trim());
        } else {
            // Admin login validation
            isValid = !etAdminUsername.getText().toString().trim().isEmpty() &&
                    !etAdminPassword.getText().toString().trim().isEmpty();
        }

        btnLogin.setEnabled(isValid);
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        // Check if phone has exactly 10 digits
        return phone.matches("\\d{10}");
    }

    private void handleLogin() {
        if (radioUser.isChecked()) {
            registerUser();
        } else {
            loginAdmin();
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (!isValidEmail(email)) {
            etEmail.setError("Invalid email format");
            return;
        }

        if (!isValidPhone(phone)) {
            etPhone.setError("Phone must be 10 digits");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAdmin(false);

        long id = databaseHelper.addUser(user);
        if (id != -1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("userId", id);
            editor.putString("userName", name);
            editor.putBoolean("isAdmin", false);
            editor.apply();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginAdmin() {
        String username = etAdminUsername.getText().toString().trim();
        String password = etAdminPassword.getText().toString().trim();

        try {
            User admin = databaseHelper.authenticateAdmin(username, password);
            if (admin != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("userId", admin.getId());
                editor.putString("userName", admin.getName());
                editor.putBoolean("isAdmin", true);
                editor.apply();

                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}