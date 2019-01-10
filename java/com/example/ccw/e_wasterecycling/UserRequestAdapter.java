package com.example.ccw.e_wasterecycling;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.suke.widget.SwitchButton;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Product> products;
    private AdapterView.OnItemClickListener mListener;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageTask mUploadTask;
    private String link;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    public UserRequestAdapter(Context context, List<Product> product) {
        mContext = context;
        products = product;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.admin_user_request_adapter, viewGroup, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info");
        storageReference = FirebaseStorage.getInstance().getReference("Product Info");
        progressDialog = new ProgressDialog(mContext);


        return new ImageViewHolder(view);
    }


    public void showProduct(Product newProduct) {
        products.add(newProduct);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final UserRequestAdapter.ImageViewHolder imageViewHolder, int i) {

        Product product = products.get(i);
        imageViewHolder.email.setText(product.getEmail());
        imageViewHolder.id.setText(product.getUid());
        imageViewHolder.tat.setText(product.getTo_available_time());
        imageViewHolder.tad.setText(product.getTo_available_date());
        imageViewHolder.fad.setText(product.getFrom_available_date());
        imageViewHolder.fat.setText(product.getFrom_available_time());
        imageViewHolder.condition.setText(product.getCondition());
        imageViewHolder.status.setText(product.getStatus());
        imageViewHolder.address.setText(product.getAddress());
        imageViewHolder.categories.setText(product.getCategory());

        Picasso.get().load(product.getImageUrl())
                .fit()
                .centerCrop()
                .into(imageViewHolder.imageView);

        imageViewHolder.switchButton.setChecked(false);
        imageViewHolder.switchButton.isChecked();
        imageViewHolder.switchButton.toggle();     //switch state
        imageViewHolder.switchButton.toggle(false);//switch without animation
        imageViewHolder.switchButton.setShadowEffect(true);//disable shadow effect
        imageViewHolder.switchButton.setEnableEffect(true);//disable the switch animation

        final String Email = imageViewHolder.email.getText().toString();
        final String Id = imageViewHolder.id.getText().toString();
        final String Tat = imageViewHolder.tat.getText().toString();
        final String Tad = imageViewHolder.tad.getText().toString();
        final String Fad = imageViewHolder.fad.getText().toString();
        final String Fat = imageViewHolder.fat.getText().toString();
        final String Condition = imageViewHolder.condition.getText().toString();
        final String Status = imageViewHolder.status.getText().toString();
        final String Address = imageViewHolder.address.getText().toString();
        final String Categories = imageViewHolder.categories.getText().toString();


        imageViewHolder.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
               AddProduct(Email,Id,Tat,Tad,Fad,Fat,Condition,Status,Address,Categories);
            }
        });



    }

    public void AddProduct(final String email,final String id,final String tat,final String tad,final String fad,
                           final String fat, final String condition, final String status,
                           final String address, final String categories){

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info").child(id);

        status.replace(" ","accepted");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (imageUri != null) {
                        final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                                + "." + getFileExtension(imageUri));
                        Toasty.success(mContext, "1", Toast.LENGTH_LONG).show();

                        mUploadTask = fileReference.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUri) {
                                                link = downloadUri.toString();
                                                Toasty.success(mContext, "Upload successful", Toast.LENGTH_LONG).show();
                                                final Product product = new Product(email,id,address,condition,link,fad,tat,fat,tad,status,categories);
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
    public int getItemCount() {
        return products.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView fad, tad, fat, tat, condition, status, address, email, id, categories;
        public ImageView imageView;
        public SwitchButton switchButton;

        public ImageViewHolder(View itemView) {
            super(itemView);

            categories = itemView.findViewById(R.id.categories);
            email = itemView.findViewById(R.id.email);
            id = itemView.findViewById(R.id.id);
            fad = itemView.findViewById(R.id.from_available_date);
            tad = itemView.findViewById(R.id.to_available_date);
            fat = itemView.findViewById(R.id.from_available_time);
            tat = itemView.findViewById(R.id.to_available_time);
            condition = itemView.findViewById(R.id.product_condition);
            status = itemView.findViewById(R.id.product_status);
            address = itemView.findViewById(R.id.user_address);
            imageView = itemView.findViewById(R.id.image);
            switchButton = itemView.findViewById(R.id.switch_button);

        }


    }


}
