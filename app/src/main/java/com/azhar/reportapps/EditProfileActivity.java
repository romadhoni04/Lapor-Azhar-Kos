package com.azhar.reportapps;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editEmail, editUsername, editPassword;
    Button saveButton;
    String nameUser, emailUser, usernameUser, passwordUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        reference = FirebaseDatabase.getInstance().getReference("users");

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);

        // Menampilkan data yang ada di Intent
        showData();

        // Menangani klik tombol simpan
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menyimpan perubahan jika ada
                boolean nameChanged = isNameChanged();
                boolean emailChanged = isEmailChanged();
                boolean passwordChanged = isPasswordChanged();

                if (nameChanged || emailChanged || passwordChanged){
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Mengecek dan menyimpan perubahan nama
    private boolean isNameChanged() {
        String newName = editName.getText().toString();
        if (!nameUser.equals(newName)) {
            reference.child(usernameUser).child("name").setValue(newName);
            nameUser = newName;
            return true;
        }
        return false;
    }

    // Mengecek dan menyimpan perubahan email
    private boolean isEmailChanged() {
        String newEmail = editEmail.getText().toString();
        if (!emailUser.equals(newEmail)) {
            reference.child(usernameUser).child("email").setValue(newEmail);
            emailUser = newEmail;
            return true;
        }
        return false;
    }

    // Mengecek dan menyimpan perubahan password
    private boolean isPasswordChanged() {
        String newPassword = editPassword.getText().toString();
        if (!passwordUser.equals(newPassword)) {
            reference.child(usernameUser).child("password").setValue(newPassword);
            passwordUser = newPassword;
            return true;
        }
        return false;
    }

    // Menampilkan data dari Intent ke EditText
    private void showData() {
        Intent intent = getIntent();
        if (intent != null) {
            nameUser = intent.getStringExtra("name");
            emailUser = intent.getStringExtra("email");
            usernameUser = intent.getStringExtra("username");
            passwordUser = intent.getStringExtra("password");

            // Mengisi EditText dengan data yang diterima
            editName.setText(nameUser);
            editEmail.setText(emailUser);
            editUsername.setText(usernameUser);
            editPassword.setText(passwordUser);
        }
    }
}
