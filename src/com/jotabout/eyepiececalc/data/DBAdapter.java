package com.jotabout.eyepiececalc.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database adapter.  Initially based on skeleton code from Listing 7-1
 * of "Professional Android 2 Application Development" by Reto Meier,
 * Wrox Press, 2010.
 * 
 * @author portuesi
 *
 */
public class DBAdapter {
	
	private static final String DATABASE_NAME = "epDatabase.db";
	private static final int DATABASE_VERSION = 1;
	
	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "_id";

	// The name and column index of each column in your database.
	// The following are assumed to be present in all model objects.
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 1;

	// Variable to hold the database instance
	private SQLiteDatabase db;
	
	// Context of the application using the database.
	private final Context context;
	
	// Database open/upgrade helper
	private myDbHelper dbHelper;
	
	// List of Migrations to execute for creating, upgrading tables for each DAO class
	private static final ArrayList<Migration> migrations = new ArrayList<Migration>();

	/**
	 * Constructor.
	 * 
	 * @param _context
	 */
	public DBAdapter(Context _context) {
		context = _context;
		dbHelper = new myDbHelper( context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	/**
	 * Open the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public DBAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close the database.
	 */
	public void close() {
		db.close();
	}
	
	/**
	 * Return a reference to the underlying SQLite database.
	 * @return
	 */
	public SQLiteDatabase getDatabase() {
		return db;
	}
	
	/**
	 * Add a migration for the Adapter to manage.  Each migration creates
	 * or upgrades one or more database tables at the time the database
	 * is opened.
	 * 
	 * @param m
	 */
	public void addMigration( Migration m ) {
		migrations.add( m );
	}

	private static class myDbHelper extends SQLiteOpenHelper {

		public myDbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			for ( Migration m : migrations ) {
				m.onCreate( _db );
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			for ( Migration m : migrations ) {
				m.onUpgrade( _db, _oldVersion, _newVersion );
			}
		}
	}
}