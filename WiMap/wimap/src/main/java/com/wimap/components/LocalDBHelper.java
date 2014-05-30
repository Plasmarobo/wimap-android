package com.wimap.components;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_ROUTERS = "router_info";
	public static final String COLUMN_SSID = "ssid";
	public static final String COLUMN_UID = "uid";
	public static final String COLUMN_SITE_ID = "site_id";
	public static final String COLUMN_POWER = "power";
	public static final String COLUMN_FREQ = "frequency";
	public static final String COLUMN_X = "x";
	public static final String COLUMN_Y = "y";
	public static final String COLUMN_Z = "z";
    public static final String COLUMN_TX_POWER = "tx_power";
	
  private static final String DATABASE_NAME = "room.db";
  private static final int DATABASE_VERSION = 8;

  public LocalDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
      try {
          database.execSQL("create table " + TABLE_ROUTERS +
                  " (id integer primary key autoincrement, " +
                  COLUMN_SSID + " text not null, " +
                  COLUMN_UID + " text not null, " +
                  COLUMN_SITE_ID + " integer not null, " +
                  COLUMN_POWER + " double not null, " +
                  COLUMN_FREQ + " double not null, " +
                  COLUMN_X + " double not null, " +
                  COLUMN_Y + " double not null, " +
                  COLUMN_Z + " double not null, " +
                  COLUMN_TX_POWER + " double not null);");
      }catch(SQLException e){
          Log.e("SQLite", e.getMessage());
      }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    Log.w(LocalDBHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTERS);
    onCreate(db);
  }

} 