package com.example.ccw.e_wasterecycling;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class UserProfile extends Fragment {

    private final static int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "MyActivity";
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String link;
    private StorageTask mUploadTask;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private CircleImageView circleImageView;
    private EditText emailText, nameText, passwordText, confirmPasswordText, mobileText, addressText;
    private TextView save;
    private List<String> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.user_profile, container, false);
        // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        emailText = (EditText) view.findViewById(R.id.input_email);
        nameText = (EditText) view.findViewById(R.id.input_name);
        passwordText = (EditText) view.findViewById(R.id.input_password);
        confirmPasswordText = (EditText) view.findViewById(R.id.input_reEnterPassword);
        mobileText = (EditText) view.findViewById(R.id.input_mobile);
        addressText = (EditText) view.findViewById(R.id.input_address);
        circleImageView = (CircleImageView) view.findViewById(R.id.circleImageView);
        save = (TextView) view.findViewById(R.id.Save);

        progressDialog = new ProgressDialog(getContext());

        emailText.setEnabled(false);

        userList = new ArrayList<>();

        String Email = getArguments().getString("Email");

        String email = Email.replace(".", ",");

        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(email);

        storageReference = FirebaseStorage.getInstance().getReference("User");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userList.add(String.valueOf(snapshot.getValue()));
                    }

                    emailText.setText(userList.get(2));
                    nameText.setText(userList.get(6));
                    passwordText.setText(userList.get(4));
                    confirmPasswordText.setText(userList.get(1));
                    mobileText.setText(userList.get(5));
                    addressText.setText(userList.get(0));

                    Picasso.get().load(userList.get(3)).into(circleImageView);

                    return;
                }


                if (!dataSnapshot.exists()) {
                    Toast.makeText(getActivity(), "Data not exist!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAccount();

            }
        });

        return view;
    }


    public void selectImage() {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);

    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
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

    public void UpdateAccount() {

        final String email = emailText.getText().toString();
        final String username = nameText.getText().toString();
        final String pass = passwordText.getText().toString();
        final String phone = mobileText.getText().toString();
        final String comfirmpass = confirmPasswordText.getText().toString();
        final String address = addressText.getText().toString();


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
            confirmPasswordText.setError("Confirm password is required");
            confirmPasswordText.requestFocus();
            return;
        }

        if ((TextUtils.isEmpty(address))) {
            addressText.setError("address is required");
            addressText.requestFocus();
            return;
        }

        progressDialog.setMessage("Updating your account");
        progressDialog.show();

        UpdateUser(email, username, pass, phone, comfirmpass, address);
        Log.d(TAG, "Adding");

    }

    public void UpdateUser(final String UserEmail, final String Username, final String Password,
                           final String PhoneNumber, final String confirmPassword, final String Address) {

        //first we encode the email into "," to enable check the firebase database
        final String email = UserEmail.replace(".", ",");


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
                                            final User user = new User(email, Username, Password, PhoneNumber, confirmPassword,
                                                    Address, link);
                                            databaseReference.setValue(user);

                                            Toasty.success(getActivity().getApplicationContext(), "Update Successful!!", Toast.LENGTH_SHORT, true).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "error" + e);
                                }
                            });
                }

            }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

}