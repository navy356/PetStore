package com.example.PetStore.helpers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.PetStore.datamodel.PetModal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetAdapterSQL extends Activity {
    private static String dbName = "PetDB";
    private static int dbVersion = 3;
    private PetdbHelper helper;
    private SQLiteDatabase petDb;
    FirebaseFirestore db;
    private Context context;

    public PetAdapterSQL(Context context){
        this.context = context;
        helper = new PetdbHelper(context, dbName, null, dbVersion);
        db = FirebaseFirestore.getInstance();
    }

    public void open(){
        petDb = helper.getWritableDatabase();
    }

    private class SyncRemoteWithLocal extends AsyncTask<Void, Void, Integer> {
        private PetAdapterSQL context;
        public SyncRemoteWithLocal(PetAdapterSQL context){
            this.context = context;
        }
        protected Integer doInBackground(Void... voids) {
            ArrayList<PetModal> pets= this.context.getPets(null, null);
            Map<Integer, Object> ids = new HashMap<>();
            for(PetModal pet : pets) {
                ids.put(pet.getId(), pet);
            }
            db.collection("Pets").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // after getting the data we are calling on success method
                            // and inside this method we are checking if the received
                            // query snapshot is empty or not.
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // if the snapshot is not empty we are hiding
                                // our progress bar and adding our data in a list.
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    // after getting this list we are passing
                                    // that list to our object class.
                                    PetModal petModal = d.toObject(PetModal.class);
                                    int id = petModal.getId();
                                    if (ids.get(id)==null){
                                        db.collection("Pets").document(Integer.toString(id))
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("", "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("", "Error deleting document", e);
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // we are displaying a toast message
                            // when we get any error from Firebase.
                        }
                    });
            PetAdapterSQL context = this.context;
            for(PetModal pet : pets)
            {
                Map<String, Object> contentValues = new HashMap<>();
                contentValues.put("id", pet.getId());
                contentValues.put("owner", pet.getOwner());
                contentValues.put("breed", pet.getBreed());
                contentValues.put("contact", pet.getContact());
                contentValues.put("location", pet.getLocation());
                contentValues.put("price", pet.getPrice());
                contentValues.put("img", pet.getImg());
                db.collection("Pets")
                        .document(Integer.toString(pet.getId()))
                        .set(contentValues)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error writing document", e);
                            }
                        });

            }
            return 1;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute(Integer result) {
            processSyncRWL(result);
        }
    }

    private class SyncLocalWithRemote extends AsyncTask<Void, Void, Integer> {
        private PetAdapterSQL context;
        public SyncLocalWithRemote(PetAdapterSQL context){
            this.context = context;
        }
        protected Integer doInBackground(Void... voids) {
            PetAdapterSQL context = this.context;
            ArrayList<PetModal> pets = context.getPets(null,null);
            Map<Integer, Object> ids = new HashMap<>();
            for(PetModal pet : pets) {
                ids.put(pet.getId(), pet);
            }
            db.collection("Pets").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // after getting the data we are calling on success method
                            // and inside this method we are checking if the received
                            // query snapshot is empty or not.
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // if the snapshot is not empty we are hiding
                                // our progress bar and adding our data in a list.
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    // after getting this list we are passing
                                    // that list to our object class.
                                    PetModal petModal = d.toObject(PetModal.class);
                                    int id = petModal.getId();
                                    PetModal p = context.getPetById(id);
                                    if (p == null)
                                    {
                                        context.insertPet(petModal,true);
                                    }
                                    else{
                                        context.updatePetById(petModal,id);
                                    }
                                    ids.remove(id);
                                }
                                for(Map.Entry<Integer,Object> id: ids.entrySet()){
                                    context.deletePetById(id.getKey().intValue());
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // we are displaying a toast message
                            // when we get any error from Firebase.
                        }
                    });
            return 1;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute(Integer result) {
            processSyncLWR(result);
        }
    }

    public void syncRWL(){
        new SyncRemoteWithLocal(this).execute();
    }

    void processSyncRWL(int result) {
        //handle value
        //Update GUI, show toast, etc..
    }

    public void syncLWR()
    {
        new SyncLocalWithRemote(this).execute();
    }

    void processSyncLWR(int result) {
        //Toast.makeText(this.context,"Synced Local With Remote: "+Integer.toString(result),Toast. LENGTH_SHORT);
        //handle value
        //Update GUI, show toast, etc..
    }


    public void insertPet(String owner, String breed, long contact, String location, int price, String img, int id, boolean addId){
        ContentValues contentValues = new ContentValues();
        if (addId)
        {
            contentValues.put("id", id);
        }
        contentValues.put("owner", owner);
        contentValues.put("breed",breed);
        contentValues.put("contact",contact);
        contentValues.put("location",location);
        contentValues.put("price",price);
        contentValues.put("img",img);
        petDb.insert("pets", null, contentValues);
        this.syncRWL();
    }

    public void insertPet(PetModal p, boolean addId){
        ContentValues contentValues = new ContentValues();
        if (addId)
        {
            contentValues.put("id", p.getId());
        }
        contentValues.put("owner", p.getOwner());
        contentValues.put("breed",p.getBreed());
        contentValues.put("contact",p.getContact());
        contentValues.put("location",p.getLocation());
        contentValues.put("price",p.getPrice());
        contentValues.put("img",p.getImg());
        petDb.insert("pets", null, contentValues);
        this.syncRWL();
    }

    public void updatePet(PetModal p,@Nullable String selection, @Nullable String[] selectionArgs){
        ContentValues contentValues = new ContentValues();
        contentValues.put("owner", p.getOwner());
        contentValues.put("breed",p.getBreed());
        contentValues.put("contact",p.getContact());
        contentValues.put("location",p.getLocation());
        contentValues.put("price",p.getPrice());
        contentValues.put("img",p.getImg());
        petDb.update("pets", contentValues, selection, selectionArgs);
        this.syncRWL();
    }

    public void updatePetById(PetModal p, int id)
    {
        String selection = "id = ?";
        String[] selectionArgs = {Integer.toString(id)};
        this.updatePet(p,selection,selectionArgs);
    }


    public void deletePets(String selection, String[] selectionArgs)
    {
        petDb.delete("pets", selection, selectionArgs);
        this.syncRWL();
    }

    public void deletePetById(int id){
        String selection = "id = ?";
        String[] selectionArgs = {Integer.toString(id)};
        this.deletePets(selection,selectionArgs);
    }

    public PetModal getPetById(int id){
        String selection = "id = ?";
        String[] selectionArgs = {Integer.toString(id)};
        ArrayList<PetModal> pets = this.getPets(selection,selectionArgs);
        int size = pets.size();
        if (size<1){
            return null;
        }
        else{
            return pets.get(0);
        }
    }

    public ArrayList<PetModal> getPets(@Nullable String selection, @Nullable String[] selectionArgs){
        ArrayList<PetModal> pets = new ArrayList<>();

        Cursor cursor = petDb.query("pets", null,
                selection, selectionArgs, null, null, null);
        while(cursor.moveToNext()) {
            PetModal p = new PetModal();
            p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            p.setOwner(cursor.getString(cursor.getColumnIndexOrThrow("owner")));
            p.setBreed(cursor.getString(cursor.getColumnIndexOrThrow("breed")));
            p.setContact(cursor.getLong(cursor.getColumnIndexOrThrow("contact")));
            p.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
            p.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow("price")));
            p.setImg(cursor.getString(cursor.getColumnIndexOrThrow("img")));
            pets.add(p);
        }
        return pets;
    }

    public void close(){
        petDb.close();
        petDb = null;
    }

    private static class PetdbHelper extends SQLiteOpenHelper {

        public PetdbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE pets( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                    "owner TEXT NOT NULL" + "," +
                    "breed TEXT NOT NULL" + "," +
                    "location TEXT NOT NULL" + "," +
                    "contact INTEGER NOT NULL" + "," +
                    "price INTEGER NOT NULL" + "," +
                    "img TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE pet");
            this.onCreate(sqLiteDatabase);
        }
    }
}
