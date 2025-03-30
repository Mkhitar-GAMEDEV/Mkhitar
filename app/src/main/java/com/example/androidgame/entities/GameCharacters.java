package com.example.androidgame.entities;

import com.example.androidgame.Helpers.GameConstants;
import com.example.androidgame.Helpers.Interfaces.BitmapMethods;
import com.example.androidgame.MainActivity;
import com.example.androidgame.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public enum GameCharacters  implements BitmapMethods {

    PLAYER(R.drawable.player_spritesheet),
    SKELETON(R.drawable.skeleton_spritesheet);

    private Bitmap spritesheet;
    private Bitmap[][] sprites = new Bitmap[7][4];


    GameCharacters(int resID){
        options.inScaled = false;
        spritesheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(),resID,options);
        for(int j = 0; j < sprites.length; j++){
            for (int i = 0; i < sprites[j].length;i++){
                sprites[j][i] = getScaledBitmap(Bitmap.createBitmap(spritesheet, GameConstants.sprites.DEFAULT_SIZE *i,GameConstants.sprites.DEFAULT_SIZE*j,GameConstants.sprites.DEFAULT_SIZE,GameConstants.sprites.DEFAULT_SIZE));
            }
        }
        System.out.println("Width: " + spritesheet.getWidth()+"Height: " + spritesheet.getHeight());
    }


    public Bitmap getSpritesheet() {
        return spritesheet;
    }
    public Bitmap getSprite(int yPos, int xPos){
        return sprites[yPos][xPos];
    }


}
