package com.example.ccw.e_wasterecycling;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class UserRequestFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {


    private final static int PICK_IMAGE_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-20, -20), new LatLng(10, 10)
    );
    private final String status = "Pending";
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    View view;
    private AutoCompleteTextView autoCompleteTextView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button button;
    private CircleImageView circleImageView;
    private ProgressDialog progressDialog;
    private TextView date_1, date_2, time_1, time_2;
    private Spinner spinner;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private StorageTask mUploadTask;
    // private NotificationManagerCompat notificationManagerCompat;
    private String link;
    private Uri imageUri;
    private PlaceAutocompleteAdapter autocompleteAdapter;
    private GoogleApiClient googleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.user_request_fragment, container, false);

        date_1 = (TextView) view.findViewById(R.id.date_1);
        date_2 = (TextView) view.findViewById(R.id.date_2);
        time_1 = (TextView) view.findViewById(R.id.time_1);
        time_2 = (TextView) view.findViewById(R.id.time_2);

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.auto_complete);
        radioGroup = (RadioGroup) view.findViewById(R.id.condition);
        button = (Button) view.findViewById(R.id.submit);
        circleImageView = (CircleImageView) view.findViewById(R.id.imageview);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.Categories,
                R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Toasty.success(getActivity(), parent.getItemAtPosition(position) + " Selected", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toasty.warning(getActivity(), "Please select a category for your product", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        progressDialog = new ProgressDialog(getActivity());

        firebaseDatabase = FirebaseDatabase.getInstance();

        // notificationManagerCompat = NotificationManagerCompat.from(getContext());


        googleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        autocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), googleApiClient,
                LAT_LNG_BOUNDS, null);

        autoCompleteTextView.setAdapter(autocompleteAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info");
        storageReference = FirebaseStorage.getInstance().getReference("Product Info");

        //get date
        calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        //get time
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        date_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        date_1.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                    }
                }, year, month, day); //changed from day,month,year   to  year,month,day
                datePickerDialog.show();
            }

        });

        date_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog = new DatePickerDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        date_2.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                    }
                }, year, month, day); //changed from day,month,year   to  year,month,day
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        time_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePickerDialog = new TimePickerDialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_1.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        time_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timePickerDialog = new TimePickerDialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time_2.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckProduct();

            }
        });


        return view;
    }



/*
    public void sendOnChannel1(View view) {

        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.cenviro)
                .setContentTitle("Hello World")
                .setContentText("hahahahah")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManagerCompat.notify(1, notification);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage((getActivity()));
            googleApiClient.disconnect();
        }
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

    public void CheckProduct() {
        int RadioButtonId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) view.findViewById(RadioButtonId);

        final String address = autoCompleteTextView.getText().toString();
        final String f_a_d = date_1.getText().toString();
        final String f_a_t = time_1.getText().toString();
        final String t_a_d = date_2.getText().toString();
        final String t_a_t = time_2.getText().toString();
        final String condition = radioButton.getText().toString();
        final String categories = spinner.getSelectedItem().toString();

        if ((TextUtils.isEmpty(address))) {
            autoCompleteTextView.setError("address is required");
            autoCompleteTextView.requestFocus();
            return;
        }
        if (date_1.getText().toString().equals("Select date")) {
            date_1.setError("Please choose your available date");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date_2.getText().toString().equals("Select date")) {
            date_2.setError("Please choose your available date");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time_1.getText().toString().equals("Select time")) {
            time_1.setError("Please choose your available time");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time_2.getText().toString().equals("Select time")) {
            time_2.setError("Please choose your available time");
            Toasty.warning(getActivity(), "Please select your available time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinner.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView) spinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Selected an category");
            spinner.requestFocus();
        }


        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toasty.warning(getActivity(), "Please Select Product Condition", Toast.LENGTH_SHORT).show();

        }

        progressDialog.setMessage("Submitting your product");
        progressDialog.show();

        String Email = getArguments().getString("Email");
        String key = firebaseDatabase.getReference("Product Info").push().getKey();

        AddProduct(Email, key, address, condition, f_a_d, f_a_t, t_a_d, t_a_t, status, categories);
    }

    private void AddProduct(final String Email, final String key, final String address, final String condition, final String f_a_d,
                            final String f_a_t, final String t_a_t, final String t_a_d, final String categories, final String status) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info").child(key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
                                                Toasty.success(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                                                final Product product = new Product(Email, key, address, condition, link, f_a_d, t_a_t, f_a_t, t_a_d
                                                        , status, categories
                                                );
                                                databaseReference.setValue(product);
                                                progressDialog.dismiss();
                                                //sendOnChannel1(view);
                                            }
                                        });
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
