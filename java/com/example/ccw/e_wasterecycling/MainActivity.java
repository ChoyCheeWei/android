package com.example.ccw.e_wasterecycling;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView imageView;
    TextView tvemail;
    Fragment fragment = null;

    private DatabaseReference databaseReference;
    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.add);

        Intent intent = getIntent();
        String email = intent.getStringExtra("Email");

        View view = navigationView.getHeaderView(0);

        imageView = (CircleImageView) view.findViewById(R.id.imageView);
        tvemail = (TextView) view.findViewById(R.id.email);
        tvemail.setText("Hello " + email);

        String Email = email.replace(".", ",");
        userList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Email);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userList.add(String.valueOf(snapshot.getValue()));
                    }


                    Picasso.get().load(userList.get(3)).into(imageView);

                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        //    showHome();
    }


    @Override
    public void onBackPressed() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        /*
        else {
            if (fragment instanceof UserRequestFragment){
                super.onBackPressed();
            }
            else {
                showHome();
            }
        }
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            Toasty.info(getApplicationContext(), "Good bye " + tvemail.getText().toString().replace("Hello", ""), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /*  private void showHome(){

          fragment = new UserRequestFragment();

          if (fragment != null){
              FragmentManager fragmentManager = getSupportFragmentManager();
              FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
              fragmentTransaction.add(R.id.frame_layout,fragment);
              fragmentTransaction.commit();
          }

      }
  */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();


        if (id == R.id.profile) {

            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new UserProfile();
            fragment.setArguments(bundle);

        } else if (id == R.id.add) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new UserRequestFragment();
            fragment.setArguments(bundle);


        } else if (id == R.id.view) {
            Bundle bundle = new Bundle();
            bundle.putString("Email", getIntent().getStringExtra("Email"));
            fragment = new ViewUserRequestFragment();
            fragment.setArguments(bundle);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
