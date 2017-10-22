package com.beastpotato.selfielight.services;

import android.content.Intent;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import com.beastpotato.selfielight.views.MainActivity;

/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Oleksiy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class MainTileService extends TileService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onClick() {
        if(Settings.canDrawOverlays(this)) {
            if(getQsTile().getState()!= Tile.STATE_ACTIVE){
                getQsTile().setState(Tile.STATE_ACTIVE);
                getQsTile().updateTile();
                showOnTopView();
            }else{
                getQsTile().setState(Tile.STATE_INACTIVE);
                getQsTile().updateTile();
                hideOnTopView();
            }
        }else{
            Toast.makeText(this, "Please Give Permission First", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    private void hideOnTopView(){
        ViewIntentService.startToggleUI(getApplicationContext(),false);
    }
    private void showOnTopView(){
        ViewIntentService.startToggleUI(getApplicationContext(),true);
    }

}
