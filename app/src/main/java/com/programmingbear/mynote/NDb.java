package com.programmingbear.mynote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by satish on 20/10/2016.
 */
public class NDb extends SQLiteOpenHelper {

    public static final String dbname="MyNotes.db";
    public static final String _id="_id";
    public static final String name="name";
    public static final String remark="remark";
    public static final String dates="dates";
    public static final String mynotes="mynotes";
    public static final String signInDetails="signInDetails";

    SQLiteDatabase db;

    public NDb(Context context){

        super(context, dbname, null, 1);
    }

    //Creates the database on start of the activity
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table mynotes"
                + "(_id integer primary key, name text,remark text,dates text,isStarred integer)");

        db.execSQL("create table signInDetails"
                + "(_id integer primary key, name text,emailId text,photoUrl text)");
    }

    //Used to update the database like adding tables,deleting tables, columns ,rows etc.,
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + mynotes);
        onCreate(db);
    }

    //Used to fetch all the notes from the database
    public Cursor fetchAll(){
        db=this.getReadableDatabase();
        Cursor mCursor=db.query(mynotes, new String[] { "_id", "name",
                "dates", "remark","isStarred" }, null, null, null, null, null);
        if(mCursor!=null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //Used to fetch signInDetails from the database
    public Cursor fetchSignInDetails(){
        db=this.getReadableDatabase();
        Cursor mCursor=db.query(signInDetails, new String[] { "name",
                "emailId", "photoUrl" }, null, null, null, null, null);
        if(mCursor!=null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    //Used to insert notes into the database
    public boolean insertNotes(String name, String dates,String remark,int isStarred){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("dates", dates);
        contentValues.put("remark", remark);
        contentValues.put("isStarred", isStarred);
        db.insert(mynotes,null,contentValues);
        return true;
    }

    //Used to insert gplus signInDetails into the database
    public boolean insertSignInDetails(String name,String emailId,String photoUrl){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",name);
        contentValues.put("emailId", emailId);
        contentValues.put("photoUrl",photoUrl);
        db.insert(signInDetails,null,contentValues);

        return true;
    }

    //Used to getData for the given id
    public Cursor getData(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor z=db.rawQuery("select * from " + mynotes + " where _id=" + id
                + "", null);
        return z;
    }

    //Used to get the no of. rows in the database
    public int numberOfRows(){
        SQLiteDatabase db=this.getReadableDatabase();
        int numRows= (int)DatabaseUtils.queryNumEntries(db,mynotes);
        return numRows;
    }

    //Used to update the record mentioned by the given id
    public boolean updateNotes(int id,String name,String dates,String remark){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name", name);
        contentValues.put("dates",dates);
        contentValues.put("remark",remark);
        db.update(mynotes,contentValues,"_id=?",new String[]{Integer.toString(id)});
        return true;
    }

    //Used to delete a record mentioned by the given id
    public Integer deleteNotes(Integer id){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(mynotes,"_id=?",new String[]{Integer.toString(id)});
    }


    //Used to delete signInDetails in the table
    public Integer deleteSignInDetails(){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.delete(signInDetails,null,null);
    }

    //?????
    public ArrayList getAll(){
        ArrayList arrayList=new ArrayList();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from " + mynotes, null);
        res.moveToFirst();
        while(res.isAfterLast()==false){
            arrayList.add(res.getString(res.getColumnIndex("_id")));
            arrayList.add(res.getString(res.getColumnIndex(remark)));
            arrayList.add(res.getString(res.getColumnIndex(dates)));
            arrayList.add(res.getString(res.getColumnIndex(name)));
            res.moveToNext();
        }
        return arrayList;
    }
}
