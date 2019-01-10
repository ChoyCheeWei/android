package com.example.ccw.e_wasterecycling;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminRequestFragment extends Fragment  {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private UserRequestAdapter userRequestAdapter;
    private ValueEventListener listener;


    private List<Product> productList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.admin_request_fragment,container,false);
       // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        productList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("Product Info");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                productList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                      Product product = postSnapshot.getValue(Product.class);
                      productList.add(product);


                }
                userRequestAdapter = new UserRequestAdapter(getActivity(),productList);
                recyclerView.setAdapter(userRequestAdapter);


                userRequestAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}
