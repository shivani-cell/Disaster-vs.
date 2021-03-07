package com.example.rescuevolunteer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
 private CountryCodePicker ccp;
 private EditText phoneText;
 private EditText codeText;
 private Button continueAndNextBtn;
 private String checker="",phonenumber="";
 private RelativeLayout relativeLayout;
 private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
 private FirebaseAuth mauth;
 private String mVerificationid;
 private PhoneAuthProvider.ForceResendingToken mresendtoken;
 private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mauth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
phoneText = findViewById(R.id.phoneText);
codeText = findViewById(R.id.codeText);
continueAndNextBtn = findViewById(R.id.continueNextButton);
relativeLayout = findViewById(R.id.phoneAuth);
ccp = (CountryCodePicker) findViewById(R.id.ccp);
ccp.registerCarrierNumberEditText(phoneText);
continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(continueAndNextBtn.getText().equals("Submit") || checker.equals("Code Sent")){
String verificationcode = codeText.getText().toString();
if(verificationcode.equals("")){
    Toast.makeText(RegisterActivity.this,"Please write verification code first",Toast.LENGTH_SHORT).show();
}
else{
    loadingBar.setTitle("Code Verification");
    loadingBar.setMessage("Please Wait... while we are verifying code");
    loadingBar.setCanceledOnTouchOutside(false);
    loadingBar.show();
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationid,verificationcode);
    signInWithPhoneAuthCredential(credential);
}
        }
        else{
            phonenumber = ccp.getFullNumberWithPlus();
            if(!phonenumber.equals("")){
loadingBar.setTitle("Phone Number Verification");
loadingBar.setMessage("Please Wait...");
loadingBar.setCanceledOnTouchOutside(false);
loadingBar.show();
               PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber,60,TimeUnit.SECONDS,RegisterActivity.this,mCallbacks);
            }
            else{
                Toast.makeText(RegisterActivity.this,"Please write valid phone number",Toast.LENGTH_SHORT).show();
            }
        }
    }
});
mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    @Override
    public void onVerificationFailed(@NonNull FirebaseException e) {
Toast.makeText(RegisterActivity.this,"Invalid phone number",Toast.LENGTH_SHORT).show();
loadingBar.dismiss();
relativeLayout.setVisibility(View.VISIBLE);
    continueAndNextBtn.setText("Continue");
    codeText.setVisibility(View.VISIBLE);
    }


    @Override
    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        super.onCodeSent(s, forceResendingToken);
        mVerificationid = s;
        mresendtoken = forceResendingToken;
    relativeLayout.setVisibility(View.GONE);
    checker="Code Sent";
    continueAndNextBtn.setText("Submit");
    codeText.setVisibility(View.VISIBLE);
    loadingBar.dismiss();
    Toast.makeText(RegisterActivity.this,"Code has been sent successfully",Toast.LENGTH_SHORT).show();
    }
};
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this,"Congrats",Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        } else {
                           loadingBar.dismiss();
                           String s=task.getException().toString();
                           Toast.makeText(RegisterActivity.this,"Error: "+s,Toast.LENGTH_SHORT).show();
                            }
                    }
                });
    }
    private void sendUserToMainActivity(){
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}