package com.example.PetStore.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.PetStore.PetDisplay;
import com.example.PetStore.PetList;
import com.example.PetStore.datamodel.PetModal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import PetStore.R;

public class PetAdapter extends ArrayAdapter<PetModal> {

    PetList plcontext;
    private ArrayList<Boolean> ownerArrayList;
    // constructor for our list view adapter.
    public PetAdapter(@NonNull Context context, ArrayList<PetModal> dataModalArrayList, ArrayList<Boolean> ownerArrayList, PetList plcontext) {
        super(context, 0, dataModalArrayList);
        this.setOwnerArrayList(ownerArrayList);
        this.plcontext = plcontext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.pet_card, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        PetModal petModal = getItem(position);
        Boolean owner = getOwnerArrayList().get(position);

        // initializing our UI components of list view item.
        ImageView petImage = listitemView.findViewById(R.id.petImage);
        TextView petText = listitemView.findViewById(R.id.petText);
        TextView petPrice = listitemView.findViewById(R.id.petPrice);

        Button petEdit = (Button) listitemView.findViewById(R.id.petEdit);
        Button petDelete = (Button) listitemView.findViewById(R.id.petDelete);

        if(owner)
        {
            petEdit.setText("Edit");
            petDelete.setText("Delete");
        }

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        petText.setText(petModal.getBreed());
        petPrice.setText(Integer.toString(petModal.getPrice()));
        Picasso.get().load(petModal.getImg()).into(petImage);

        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Intent intent = new Intent(v.getContext(), PetDisplay.class);
                Bundle b = new Bundle();
                b.putInt("petId",petModal.getId()); //Your id
                b.putChar("mode",'V');
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        petEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Intent intent = new Intent(v.getContext(), PetDisplay.class);
                Bundle b = new Bundle();
                b.putInt("petId",petModal.getId()); //Your id
                Button petEdit = (Button) v.findViewById(R.id.petEdit);
                if (petEdit.getText().equals("Edit"))
                {
                    b.putChar("mode",'E');
                }
                else{
                    b.putChar("mode",'V');
                }
                intent.putExtras(b);
                plcontext.someActivityResultLauncher.launch(intent);
            }
        });

        petDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Intent intent = new Intent(v.getContext(), PetDisplay.class);
                Bundle b = new Bundle();
                Button petEdit = (Button) v.findViewById(R.id.petDelete);
                b.putInt("petId",petModal.getId()); //Your id
                if (petEdit.getText().equals("Delete"))
                {
                    b.putChar("mode",'D');
                }
                else{
                    b.putChar("mode",'B');
                }
                intent.putExtras(b);
                plcontext.someActivityResultLauncher.launch(intent);
            }
        });

        return listitemView;
    }

    public ArrayList<Boolean> getOwnerArrayList() {
        return ownerArrayList;
    }

    public void setOwnerArrayList(ArrayList<Boolean> ownerArrayList) {
        this.ownerArrayList = ownerArrayList;
    }

}
