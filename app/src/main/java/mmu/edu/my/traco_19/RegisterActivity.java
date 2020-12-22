package mmu.edu.my.traco_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText regName, regEmail, regPassword, conPassword;
    Button regBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        regName=findViewById(R.id.register_user_text);
        regEmail=findViewById(R.id.register_email_text);
        regPassword=findViewById(R.id.register_password_text);
        conPassword=findViewById(R.id.register_conform_password_text);
        regBtn=findViewById(R.id.register_button);
        fAuth=FirebaseAuth.getInstance();

        TextView login=findViewById(R.id.already_have_account);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

        if (fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid=true;
                if (isEmpty(regName)) {
                    regName.setError("Username is required");
                    isValid=false; }

                if (isEmpty(regPassword)) {
                    regPassword.setError("Password is required");
                    isValid=false; }

                if (isEmail(regEmail)==false) {
                    regEmail.setError("Enter valid email");
                    isValid=false; }

                if (isEmpty(conPassword)) {
                    conPassword.setError("Password entered is different");
                    isValid = false;
                }

                if(!isEmpty(conPassword)) {
                    String passwordValue= regPassword.getText().toString().trim();
                    if (!(conPassword.getText().toString().trim()).equals(passwordValue) )
                        isValid=false;}


                if (isValid) {
                    String emailValue= regEmail.getText().toString().trim();
                    String passwordValue= regPassword.getText().toString().trim();
                    fAuth.createUserWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    boolean isEmail(EditText text){
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text){
        CharSequence str=text.getText().toString();
        return TextUtils.isEmpty(str);
    }

}