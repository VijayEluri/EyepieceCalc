package com.jotabout.eyepiececalc.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jotabout.eyepiececalc.model.Eyepiece;

/**
 * <p>Database adapter class for Eyepieces. Handles retrieving
 * and storing Eyepiece model objects and cursors from database.</p>
 * 
 * <p>Starting point was skeleton code from Listing 7-1 of
 * "Professional Android 2 Application Development" by Reto Meier,
 * Wrox Press, 2010.</p>
 * 
 * @author portuesi
 *
 */
public class EyepieceAdapter {
	private static final String DATABASE_NAME = "eyepieceCalc.db";
	private static final String DATABASE_TABLE = "eyepieces";
	private static final int DATABASE_VERSION = 1;

	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "_id";

	// The name and column index of each column in your database.
	public static final String KEY_NAME 			= "name";
	public static final String KEY_FOCAL_LENGTH	= "focal_length";
	public static final String KEY_APPARENT_FOV	= "apparent_fov";
	
	public static final int NAME_COLUMN			= 1;
	public static final int FOCAL_LENGTH_COLUMN	= 2;
	public static final int APPARENT_FOV_COLUMN	= 3;
	
	private static final String[] resultColumns = { KEY_ID, KEY_NAME, KEY_FOCAL_LENGTH, KEY_APPARENT_FOV };
	private static final String[] allEntriesColumns = { KEY_ID, KEY_NAME };
	
	private final static StringBuilder sb = new StringBuilder();

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null);";

	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private EyepieceDbHelper dbHelper;

	public EyepieceAdapter(Context _context) {
		context = _context;
		dbHelper = new EyepieceDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public EyepieceAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	/**
	 * Create a new ContentValues to represent my row
	 * and insert it into the database.
	 * 
	 * @param _myObject
	 * @return
	 */
	public long insertEntry(Eyepiece _myObject) {
		ContentValues newValues = new ContentValues();
		
		newValues.put( KEY_NAME, 		 _myObject.getName()        );
		newValues.put( KEY_FOCAL_LENGTH, _myObject.getFocalLength() );
		newValues.put( KEY_APPARENT_FOV, _myObject.getApparentFOV() );
		
		return db.insert( DATABASE_TABLE, null, newValues );
	}

	public boolean removeEntry(long _rowIndex) {
		sb.setLength(0);
		String where = sb.append( KEY_ID ).append( "=" ).append( Long.toString( _rowIndex) ).toString();
		return db.delete(DATABASE_TABLE, where, null) > 0;
	}

	public Cursor getAllEntries() {
		return db.query(DATABASE_TABLE, allEntriesColumns,
				null, null, null, null, null);
	}

	/**
	 * Return a cursor to a row from the database and
	 * use the values to populate an instance of Eyepiece.
	 * 
	 * @param _rowIndex
	 * @return
	 */
	public Eyepiece getEntry(long _rowIndex) {
		Eyepiece objectInstance = null;
		
		sb.setLength(0);
		Cursor resultSet = db.query( DATABASE_TABLE,
								     resultColumns,
								     sb.append( KEY_ID ).append( "=" ).append( _rowIndex ).toString(),
								     null, null, null, null);

		if ( resultSet.moveToFirst() ) {
			objectInstance = new Eyepiece( resultSet.getLong( 0 ), 
											resultSet.getString( NAME_COLUMN ),
											resultSet.getInt( FOCAL_LENGTH_COLUMN ),
											resultSet.getInt( APPARENT_FOV_COLUMN ) );
		}

		return objectInstance;
	}

	/**
	 * Create a new ContentValues based on the new object
	 * and use it to update a row in the database.
	 * 
	 * @param _rowIndex
	 * @param _myObject
	 * @return
	 */
	public boolean updateEntry(long _rowIndex, Eyepiece _myObject) {
		ContentValues newValues = new ContentValues();
		
		newValues.put( KEY_NAME, 		 _myObject.getName()        );
		newValues.put( KEY_FOCAL_LENGTH, _myObject.getFocalLength() );
		newValues.put( KEY_APPARENT_FOV, _myObject.getApparentFOV() );
		
		sb.setLength(0);
		String where = sb.append( KEY_ID ).append( "=" ).append( Long.toString( _rowIndex) ).toString();
		if ( db.update( DATABASE_TABLE, newValues, where, null ) > 0 ) {
			return true;		
		}
		
		return false;
	}

	private static class EyepieceDbHelper extends SQLiteOpenHelper {

		public EyepieceDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs
		// to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		// Called when there is a database version mismatch meaning that the
		// version of the database on disk needs to be upgraded to the current
		// version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Log the version upgrade.
			Log.w("EyepieceDbHelper", "Upgrading from version " + _oldVersion
					+ " to " + _newVersion
					+ ", which will destroy all old data");

			// Upgrade the existing database to conform to the new version.
			// Multiple previous versions can be handled by comparing
			// _oldVersion and _newVersion values.

			// The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}
}
