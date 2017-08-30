package com.example.mohamedabdelaziz.marketstore;

import java.util.ArrayList;

/**
 * Created by Mohamed Abd Elaziz on 8/8/2017.
 */

public class ProductDataTypes {

    public String image, desc, price, type, ownerid, ownerImage, ownerName, date, time;
    public ArrayList<Followers> follower;
    public ArrayList<Comments> comment;

    ProductDataTypes() {
    }

    public ProductDataTypes(ArrayList<Comments> comment, String date, String desc, ArrayList<Followers> follower
            , String image, String ownerImage, String ownerName, String ownerid
            , String price, String time, String type
    ) {
        this.image = image;
        this.desc = desc;
        this.price = price;
        this.type = type;
        this.ownerid = ownerid;
        this.ownerName = ownerName;
        this.date = date;
        this.follower = follower;
        this.comment = comment;
        this.ownerImage = ownerImage;
        this.time = time;
    }

}

class Followers {
    public String id, name;

    Followers() {
    }

    public Followers(String id, String name) {
        this.id = id;
        this.name = name;

    }
}

class Comments {
    public String id, name, comment;

    Comments() {
    }

    public Comments(String id, String name, String comment) {
        this.id = id;
        this.name = name;
        this.comment = comment;
    }

}
