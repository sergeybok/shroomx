package ch.usi.inf.mc.shroomx;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by sergiybokhnyak on 06.12.17.
 */

public class MushroomRecord {

    static final String TAG = "Mushroom_Record";

    private String name;
    private Date dateFound;
    private Bitmap img;

    public MushroomRecord(String name, Date dateFound, Bitmap img) {
        this.name = name;
        this.dateFound = dateFound;
        this.img = img;
    }

    public String getName() {
        return this.name;
    }

    public Date getDateFound() {
        return this.dateFound;
    }

    public Bitmap getImg() {
        return this.img;
    }
}
