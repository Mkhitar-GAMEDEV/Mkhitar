package com.example.androidgame.Helpers.Interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.androidgame.Helpers.GameConstants;

public interface BitmapMethods {

     BitmapFactory.Options options = new BitmapFactory.Options();

    default Bitmap getScaledBitmap(Bitmap bitmap){
        return Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()* GameConstants.sprites.SCALE_MULTIPLIER,bitmap.getHeight()*GameConstants.sprites.SCALE_MULTIPLIER,false);
    }
}
