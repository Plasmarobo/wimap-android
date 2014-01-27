package com.witech.wimap;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RouterDatabase {

  // Database fields
  private SQLiteDatabase database;
  private LocalDBHelper dbHelper;
  private String[] allColumns = { "id",
		  LocalDBHelper.COLUMN_SSID,
		  LocalDBHelper.COLUMN_UID,
		  LocalDBHelper.COLUMN_POWER,
		  LocalDBHelper.COLUMN_X,
		  LocalDBHelper.COLUMN_Y,
		  LocalDBHelper.COLUMN_Z
		  };

  public RouterDatabase(Context context) {
    dbHelper = new LocalDBHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public void WriteRouter(Router r) {
    ContentValues values = new ContentValues();
    values.put(LocalDBHelper.COLUMN_SSID, r.GetSSID());
    values.put(LocalDBHelper.COLUMN_UID, r.GetUID());
    values.put(LocalDBHelper.COLUMN_POWER, r.GetPower());
    values.put(LocalDBHelper.COLUMN_X, r.GetX());
    values.put(LocalDBHelper.COLUMN_Y, r.GetY());
    values.put(LocalDBHelper.COLUMN_Z, r.GetZ());
    
    database.insert(LocalDBHelper.TABLE_ROUTERS, null, values);
  }

  public void RemoveRouter(Router r) {
    long id = r.GetID();
   	Log.i("RouterDatabase", "Router deleted with id: " + id);
    database.delete(LocalDBHelper.TABLE_ROUTERS, "id"
        + " = " + id, null);
  }
  public List<Router> getRoutersBySSID(String ssid)
  {
	  List<Router> routers = new ArrayList<Router>();
	  Cursor cursor = database.rawQuery("SELECT * FROM " + LocalDBHelper.TABLE_ROUTERS + " WHERE SSID=?", new String[] {ssid});
	  cursor.moveToFirst();
	  while (!cursor.isAfterLast()) {
	      Router r = cursorToRouter(cursor);
	      routers.add(r);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return routers;
  }
  public Router getRouterByUID(String uid)
  {
	  
	  Cursor cursor = database.rawQuery("SELECT * FROM " + LocalDBHelper.TABLE_ROUTERS + " WHERE SSID=?", new String[] {uid});
	  cursor.moveToFirst();
	  return cursorToRouter(cursor);
  }

  public List<Router> getAllRouters() {
    List<Router> routers = new ArrayList<Router>();

    //Cursor cursor = database.query(LocalDBHelper.TABLE_ROUTERS,
    //    allColumns, null, null, null, null, null);
    Cursor cursor = database.rawQuery("SELECT * FROM " + LocalDBHelper.TABLE_ROUTERS, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Router r = cursorToRouter(cursor);
      routers.add(r);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return routers;
  }

  private Router cursorToRouter(Cursor cursor) {
    Router r = new Router();
    r.SetID(cursor.getInt(0));
    r.SetSSID(cursor.getString(1));
    r.SetUID(cursor.getString(2));
    r.SetPower(cursor.getInt(3));
    r.SetX(cursor.getInt(4));
    r.SetY(cursor.getInt(5));
    r.SetZ(cursor.getInt(6));
    return r;
  }
  public void ForceReset()
  {
	  dbHelper.onUpgrade(database, 0, 2);
  }
} 