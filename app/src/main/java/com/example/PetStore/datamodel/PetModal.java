package com.example.PetStore.datamodel;

public class PetModal {
    private String owner;
    private String breed;
    private long contact;
    private String location;
    private int price;
    private String img;
    private int id;

    public PetModal(String owner, String breed, long contact, String location, int price, String img, int id) {
        this.setId(id);
        this.setOwner(owner);
        this.setBreed(breed);
        this.setContact(contact);
        this.setLocation(location);
        this.setPrice(price);
        this.setImg(img);
    }

    public PetModal()
    {

    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public long getContact() {
        return contact;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
