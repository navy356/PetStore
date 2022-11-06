package com.example.PetStore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.PetStore.datamodel.PetModal;
import com.example.PetStore.helpers.PetAdapterSQL;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import PetStore.R;

public class PetDisplay extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // creating a variable for our list view,
    // arraylist and firebase Firestore.
    FirebaseFirestore db;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    PetAdapterSQL petdb;
    char mode;
    int petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle b = getIntent().getExtras();
        petId = -1; // or other values
        mode = 'V';
        if(b != null) {
            petId = b.getInt("petId");
            mode = b.getChar("mode");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_display);

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();
        petdb = new PetAdapterSQL(this);
        petdb.open();

        ImageView petImageDisp = (ImageView) findViewById(R.id.petImageDisp);

        Button addUrlBtn = (Button) findViewById(R.id.addUrlBtn);

        setNavigationViewListener();

        petImageDisp.setOnClickListener(v -> {
            EditText url = (EditText) findViewById(R.id.url);
            url.setVisibility(View.VISIBLE);
            addUrlBtn.setVisibility(View.VISIBLE);
        });

        addUrlBtn.setOnClickListener(v -> {
            EditText url = (EditText) findViewById(R.id.url);
            Picasso.get().load(url.getText().toString()).into(petImageDisp);
            url.setVisibility(View.GONE);
            addUrlBtn.setVisibility(View.GONE);
        });

        Button addPetBtn = (Button)findViewById(R.id.addPetBtn);


        if (mode=='V') {
            // here we are calling a method
            // to load data in our list view.
            loadData(petId,mode);
        }
        else if(mode=='E')
        {
            // here we are calling a method
            // to load data in our list view.
            loadData(petId,mode);
            addPetBtn.setText("Update");
        }
        else if(mode=='D')
        {
            // here we are calling a method
            // to load data in our list view.
            loadData(petId,mode);
            addPetBtn.setText("Delete");
        }
        else if(mode=='B')
        {
            // here we are calling a method
            // to load data in our list view.
            loadData(petId,mode);
            addPetBtn.setText("Buy");
        }
        else if(mode=='A')
        {
            addPetBtn.setText("Add");
        }

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText url = (EditText) findViewById(R.id.url);
                EditText id = (EditText) findViewById(R.id.petID);
                EditText breed = (EditText) findViewById(R.id.breed);
                EditText owner = (EditText) findViewById(R.id.ownername);
                EditText contact = (EditText) findViewById(R.id.contact);
                EditText location = (EditText) findViewById(R.id.location);
                EditText price = (EditText) findViewById(R.id.price);
                ImageView petImageDisp = (ImageView) findViewById(R.id.petImageDisp);
                PetModal petModal = new PetModal(owner.getText().toString(),
                        breed.getText().toString(),
                        Long.parseLong(contact.getText().toString()),
                        location.getText().toString(),
                        Integer.parseInt(price.getText().toString()),
                        url.getText().toString(),0);

                switch(mode){
                    case 'E':
                        petdb.updatePetById(petModal,petId);
                        finish();
                        break;
                    case 'D':
                    case 'B':
                        petdb.deletePetById(petId);
                        finish();
                        break;
                    case 'A':
                        petdb.insertPet(petModal,false);
                        finish();
                        break;
                    default:
                        finish();
                }
            }
        });
    }

    private void loadData(int petId, char mode) {
        PetModal petModal = petdb.getPetById(petId);
        EditText url = (EditText) findViewById(R.id.url);
        EditText id = (EditText) findViewById(R.id.petID);
        EditText breed = (EditText) findViewById(R.id.breed);
        EditText owner = (EditText) findViewById(R.id.ownername);
        EditText contact = (EditText) findViewById(R.id.contact);
        EditText location = (EditText) findViewById(R.id.location);
        EditText price = (EditText) findViewById(R.id.price);
        ImageView petImageDisp = (ImageView) findViewById(R.id.petImageDisp);
        if(mode!='E')
        {
            url.setBackgroundResource(android.R.color.transparent);
            url.setEnabled(false);
            id.setBackgroundResource(android.R.color.transparent);
            id.setEnabled(false);
            breed.setBackgroundResource(android.R.color.transparent);
            breed.setEnabled(false);
            owner.setBackgroundResource(android.R.color.transparent);
            owner.setEnabled(false);
            contact.setBackgroundResource(android.R.color.transparent);
            contact.setEnabled(false);
            price.setBackgroundResource(android.R.color.transparent);
            price.setEnabled(false);
            location.setBackgroundResource(android.R.color.transparent);
            location.setEnabled(false);
        }
        id.setText(Integer.toString(petModal.getId()));
        breed.setText(petModal.getBreed());
        owner.setText(petModal.getOwner());
        location.setText(petModal.getLocation());
        url.setText(petModal.getImg());
        contact.setText(Long.toString(petModal.getContact()));
        price.setText(Integer.toString(petModal.getPrice()));
        Picasso.get().load(petModal.getImg()).into(petImageDisp);
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
