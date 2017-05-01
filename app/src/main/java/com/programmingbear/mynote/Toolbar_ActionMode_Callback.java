package com.programmingbear.mynote;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/**
 * Created by satish on 4/3/2017.
 */

public class Toolbar_ActionMode_Callback implements ActionMode.Callback {
    private Context context;
    private Recycler_View_Adapter recycler_view_adapter;
    private ArrayList<note> note_models;
    private boolean isListViewFragment;
    public Toolbar_ActionMode_Callback(Context context, Recycler_View_Adapter recycler_view_adapter, ArrayList<note> note_models, boolean isListViewFragment) {
        this.context = context;
        this.recycler_view_adapter = recycler_view_adapter;
        this.note_models = note_models;
        this.isListViewFragment = isListViewFragment;
    }
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_main, menu);         //Inflate the menu over action mode
        return true;
    }
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if ( Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.delete), MenuItemCompat.SHOW_AS_ACTION_NEVER);
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.copy), MenuItemCompat.SHOW_AS_ACTION_NEVER);
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.share), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                     //delete selected rows
                break;
            case R.id.copy:
                         //Finish action mode
                break;
            case R.id.share:
                     //Finish action mode
                break;
        }
        return false;
    }
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        recycler_view_adapter.removeSelection();         // remove selection
            //Set action mode null
    }
}