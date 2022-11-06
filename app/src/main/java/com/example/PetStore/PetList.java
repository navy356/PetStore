package com.example.PetStore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.PetStore.datamodel.PetModal;
import com.example.PetStore.helpers.PetAdapter;
import com.example.PetStore.helpers.PetAdapterSQL;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import PetStore.R;

public class PetList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // creating a variable for our list view,
    // arraylist and firebase Firestore.
    ListView petsLV;
    ArrayList<PetModal> petModalArrayList;
    ArrayList<Boolean> petOwnerArrayList;
    FirebaseFirestore db;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    PetAdapterSQL petdb;
    public ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_add);

        // below line is use to initialize our variables
        petsLV = findViewById(R.id.petsLV);
        petModalArrayList = new ArrayList<>();
        petOwnerArrayList = new ArrayList<>();

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // There are no request codes
                        Toast.makeText(getApplicationContext(),"I have returned",Toast.LENGTH_SHORT);
                        Intent data = result.getData();
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();
        petdb = new PetAdapterSQL(this);
        petdb.open();

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // here we are calling a method
        // to load data in our list view.
        loadDatainListview();

        setNavigationViewListener();

        Button addPetBtn = (Button) findViewById(R.id.addPetBtn);

        addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Intent intent = new Intent(v.getContext(), PetDisplay.class);
                Bundle b = new Bundle();
                b.putInt("petId",-1); //Your id
                b.putChar("mode",'A');
                intent.putExtras(b);
                someActivityResultLauncher.launch(intent);
            }
        });
    }

    private void loadDatainListview() {
        petModalArrayList = petdb.getPets(null,null);
        SharedPreferences sp1=getSharedPreferences("Login", MODE_PRIVATE);

        String username=sp1.getString("username", null);
        for (PetModal p : petModalArrayList)
        {
            if (username.equals(p.getOwner()))
            {
                petOwnerArrayList.add(true);
            }
            else{
                petOwnerArrayList.add(false);
            }
        }

        PetAdapter adapter = new PetAdapter(PetList.this, petModalArrayList, petOwnerArrayList, this);
        petsLV.setAdapter(adapter);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            NavigationMenuItemView welcome = (NavigationMenuItemView) findViewById(R.id.welcome);
            SharedPreferences sp=getSharedPreferences("Login", MODE_PRIVATE);
            welcome.setTitle("Welcome, "+sp.getString("username",""));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                Intent intents = new Intent(this, MainActivity.class);
                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intents);
                finish();
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

}
