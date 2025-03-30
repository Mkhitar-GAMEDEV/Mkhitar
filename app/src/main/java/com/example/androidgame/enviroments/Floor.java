package com.example.androidgame.enviroments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.androidgame.Helpers.GameConstants;
import com.example.androidgame.Helpers.Interfaces.BitmapMethods;
import com.example.androidgame.MainActivity;
import com.example.androidgame.R;

public enum Floor implements BitmapMethods {
    OUTSIDE(R.drawable.tileset_floor,22,26);

    private Bitmap[] sprites;
    Floor(int resID,int tilesInWidth,int tilesInHeight){
        options.inScaled = false;
        sprites = new Bitmap[tilesInHeight * tilesInWidth];
         Bitmap spritesheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(),resID,options);
        for(int j = 0; j < tilesInHeight; j++){
            for (int i = 0; i < tilesInWidth;i++){
                int index = j * tilesInWidth + i;
                sprites[index] = getScaledBitmap(Bitmap.createBitmap(spritesheet, GameConstants.sprites.DEFAULT_SIZE*i,GameConstants.sprites.DEFAULT_SIZE*j,GameConstants.sprites.DEFAULT_SIZE,GameConstants.sprites.DEFAULT_SIZE));
//                sprites[j][i] = getScaledBitmap(Bitmap.createBitmap(spritesheet,16*i,16*j,16,16));
            }
        }
    }

    public Bitmap getSprite(int id){
        return sprites[id];
    }
}
