package com.jotabout.eyepiececalc.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Interface and shared implementation for classes that access the database
 * and CRUD for model objects.
 * 
 * @author portuesi
 *
 * @param <T>
 */
public class BaseDAO<T> {
	
	// Name of the class this DAO manages.
	protected String CLASS_NAME;

	// Name of the database table this adapter class manages.
	protected String DATABASE_TABLE;
	
	// The index (key) column name for use in where clauses.
	protected String KEY_ID = "id";
	
	// The name of each column in your database.  The ones listed
	// here are present in all database tables.
	protected String KEY_NAME = "name";

	// List of all column names
	// Subclasses must append to this list to declare the columns they
	// wish to persist to database.
	protected ArrayList<String> COLUMN_NAMES = new ArrayList<String>();
	
	// Database
	protected SQLiteDatabase db;
	protected DBAdapter dbAdapter;
	
	public BaseDAO( DBAdapter dbAdapter ) {
		this.dbAdapter = dbAdapter;
		this.db = dbAdapter.getDatabase();
		
		// Start with standard names for all model objects.
		// Subclasses must add to this list.
		COLUMN_NAMES.add( KEY_ID   );
		COLUMN_NAMES.add( KEY_NAME );
	}

	public long insertEntry(T _myObject)  {
		ContentValues newValues = new ContentValues();
		
		// Assign values for each row.
		for ( String colName : COLUMN_NAMES ) {
			
			// TODO get value from object using reflection
			// TODO cast it to a string
			
			String newValue = null;
			newValues.put(colName, newValue);
		}

		// Insert the row into your table
		return db.insert(DATABASE_TABLE, null, newValues);
	};

	public boolean removeEntry(long _rowIndex) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	}

	public Cursor getAllEntries() {
		// TODO should convert to an array of T and return
		return db.query(DATABASE_TABLE, (String[]) COLUMN_NAMES.toArray(), null, null, null, null, null);
	}

	@SuppressWarnings("unchecked")
	public T getEntry(long _rowIndex) {
		T objectInstance = null;
		
		try {
			objectInstance = (T) Class.forName( CLASS_NAME );
		
			// TODO: Return a cursor to a row from the database and
			// use the values to populate objectInstance via reflection
			// use type from object to determine which accessor on the
			// cursor to use.

		} catch (ClassNotFoundException cnfe) {;}

		return objectInstance;
	}

	public boolean updateEntry(long _rowIndex, T _myObject) {
		ContentValues newValues = new ContentValues();
		
		// Assign values for each row.
		for ( String colName : COLUMN_NAMES ) {
			
			// TODO get value from object using reflection
			// TODO cast it to a string
			
			String newValue = null;
			newValues.put(colName, newValue);
		}
		
		String where = KEY_ID + "=" + _rowIndex;
		if ( db.update(DATABASE_TABLE, newValues, where, null) > 0) {
			return true;
		}
		
		return false;
	}

}
