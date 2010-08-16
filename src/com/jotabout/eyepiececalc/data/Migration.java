package com.jotabout.eyepiececalc.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Interface to allow DAO classes to create and update database schema.
 * 
 * @author portuesi
 *
 */
public interface Migration {
	
	/**
	 * Called when no database exists in disk and the helper class needs
	 * to create a new one. 
	 *
	 * @param db
	 */
	public void onCreate(SQLiteDatabase db);

	/**
	 *  Called when there is a database version mismatch meaning that the
	 *  version of the database on disk needs to be upgraded to the current
	 *  version.
	 *  
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
