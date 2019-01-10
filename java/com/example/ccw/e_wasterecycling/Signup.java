package com.example.ccw.e_wasterecycling;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;


public class Signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private final static int PICK_IMAGE_REQUEST = 1;
    CircleImageView circleImageView;
    EditText nameText, addressText, emailText, mobileText, passwordText, reEnterPasswordText;
    Button signupButton;
    TextView loginLink;
    private DatabaseReference Userdatabase;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private StorageTask mUploadTask;
    private String link;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameText = (EditText) findViewById(R.id.input_name);
        addressText = (EditText) findViewById(R.id.input_address);
        emailText = (EditText) findViewById(R.id.input_email);
        mobileText = (EditText) findViewById(R.id.input_mobile);
        passwordText = (EditText) findViewById(R.id.input_password);
        reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        circleImageView = (CircleImageView) findViewById(R.id.imageView);

        firebaseAuth = FirebaseAuth.getInstance();

        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);

        Userdatabase = FirebaseDatabase.getInstance().getReference("User");

        progressDialog = new ProgressDialog(Signup.this);

        storageReference = FirebaseStorage.getInstance().getReference("User");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAccount();

            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public void selectImage() {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {


            imageUri = data.getData();
            Picasso.get().load(imageUri).into(circleImageView);

        }
    }


    public void RegisterAccount() {

        final String email = emailText.getText().toString();
        final String username = nameText.getText().toString();
        final String pass = passwordText.getText().toString();
        final String phone = mobileText.getText().toString();
        final String comfirmpass = reEnterPasswordText.getText().toString();
        final String address = addressText.getText().toString();

        if ((TextUtils.isEmpty(email))) {
            emailText.setError("Email address is required");
            emailText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Invalid email");
            emailText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(username))) {
            nameText.setError("Username is required");
            nameText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(pass))) {
            passwordText.setError("Password is required");
            passwordText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(phone))) {
            mobileText.setError("Phone number is required");
            mobileText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(comfirmpass))) {
            reEnterPasswordText.setError("Confirm password is required");
            reEnterPasswordText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(address))) {
            addressText.setError("address is required");
            addressText.requestFocus();
            return;
        }
        if (passwordText.length() <= 7) {
            passwordText.setError("Please Enter at least 8 digit password");
            passwordText.requestFocus();
            return;
        }
        if (reEnterPasswordText.length() <= 7) {
            reEnterPasswordText.setError("Please Enter at least 8 digit password");
            passwordText.requestFocus();
            return;
        }
        if (mobileText.length() <= 7) {
            mobileText.setError("Please Enter at least 10 digit phone number");
            mobileText.requestFocus();
            return;
        }
        if (!passwordText.getText().toString().equals(reEnterPasswordText.getText().toString())) {
            passwordText.setError("Password does not match with confirm password");
            reEnterPasswordText.setError("Password does not match with confirm password");
            passwordText.requestFocus();
            reEnterPasswordText.requestFocus();
            return;
        }

        progressDialog.setMessage("Registering your account");
        progressDialog.show();


        AddUser(email, username, pass, phone, comfirmpass, address);


    }

    public void AddUser(final String UserEmail, final String Username, final String Password,
                        final String PhoneNumber, final String confirmPassword, final String Address) {

        //first we encode the email into "," to enable check the firebase database
        String email = UserEmail.replace(".", ",");


        Userdatabase = FirebaseDatabase.getInstance().getReference("User").child(email);

        Log.d("UserEmail", Userdatabase.toString());

        Userdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    // String value = dataSnapshot.getValue(String.class);

                    Log.i(TAG, "UserEmail : " + map + " Had Already Exist");

                    Toasty.warning(getApplicationContext(), "The Email you use already Exist !", Toast.LENGTH_SHORT, true).show();
                    return;
                }


                if (!dataSnapshot.exists()) {
                    if (imageUri != null) {
                        final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                + "." + getFileExtension(imageUri));


                        mUploadTask = fileReference.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUri) {
                                                link = downloadUri.toString();
                                                Toast.makeText(Signup.this, "Register successful", Toast.LENGTH_LONG).show();
                                                final User user = new User(UserEmail, Username, Password, PhoneNumber, confirmPassword, Address,
                                                        link);
                                                Userdatabase.setValue(user);

                                            }
                                        });
                                    }
                                });
                    } else {
                        Toasty.warning(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        firebaseAuth.createUserWithEmailAndPassword(UserEmail, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if (!task.isSuccessful()) {

                    Log.i(TAG, "Buyer FirebaseAuth Register : Fail");

                    Toasty.error(getApplicationContext(), "The Email you use already Exist !", Toast.LENGTH_SHORT, true).show();
                } else {

                    Log.i(TAG, "Buyer FirebaseAuth Register : Success");
                    UserEmail.replace(".", ",");
                    final User user = new User(UserEmail, Username, Password, PhoneNumber, confirmPassword, Address,
                            link);

                    Userdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                Userdatabase.setValue(user);
                                Log.i(TAG, "FirebaseDatabase Add Buyer : Success");
                                Toasty.success(getApplicationContext(), "Register Complete", Toast.LENGTH_SHORT, true).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "Database Error");
                        }
                    });
                }
            }
        });
    }


}