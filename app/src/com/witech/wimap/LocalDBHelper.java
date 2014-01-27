package com.witech.wimap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_ROUTERS = "router_info";
	public static final String COLUMN_SSID = "ssid";
	public static final String COLUMN_UID = "uid";
	public static final String COLUMN_POWER = "power";
	public static final String COLUMN_X = "x";
	public static final String COLUMN_Y = "y";
	public static final String COLUMN_Z = "z";
	
  private static final String DATABASE_NAME = "room.db";
  private static final int DATABASE_VERSION = 2;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
      + TABLE_ROUTERS + "(id integer primary key autoincrement, " + COLUMN_SSID
      + " text not null, " + COLUMN_UID + " text not null, " + COLUMN_POWER + " text not null," + COLUMN_X + " integer," + COLUMN_Y + " integer, " + COLUMN_Z + "integer);";

  public LocalDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL("create table router_info (id integer primary key autoincrement, ssid text not null, uid text not null, power integer, x integer, y integer, z integer);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(LocalDBHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS router_info");
    onCreate(db);
  }

} 