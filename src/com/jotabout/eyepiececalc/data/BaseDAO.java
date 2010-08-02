package com.jotabout.eyepiececalc.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Interface and base implementation for DAO classes that access the
 * database, including CRUD for model objects.
 * 
 * This class contains methods that move object fields to and from the
 * database in a generic manner.  This is done via reflection to find
 * the getters and setters of the model objects.
 * 
 * DAO subclasses declare the name of the database table they manage,
 * as well as an array naming of the database columns that should be
 * persisted.  The types of the columns are determined by reflection on
 * the model objects.
 * 
 * At this time, only Strings and integer data types are supported.
 * No support for inheritance or joins is provided.
 * 
 * Initially based on skeleton code from Listing 7-1 of
 * "Professional Android 2 Application Development" by Reto Meier,
 * Wrox Press, 2010.
 * 
 * @author portuesi
 *
 * @param <T>
 */
public class BaseDAO<T> {
	
	// Name of the class this DAO manages.
	// Subclasses must set this value.
	protected String CLASS_NAME;

	// Name of the database table this adapter class manages.
	// Subclasses must set this value.
	protected String DATABASE_TABLE;
	
	// The index (key) column name for use in where clauses.
	protected final String KEY_ID = "id";
	
	// The name of each column in your database.  The ones listed
	// here are present in all database tables.
	protected final String KEY_NAME = "name";

	// List of all column names
	// Subclasses must append to this list to declare the columns they
	// wish to persist to database.
	protected final ArrayList<String> COLUMN_NAMES = new ArrayList<String>();
	
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

	/**
	 * Insert a new object into the database.
	 * @param _myObject
	 * @return ID of newly inserted object.
	 */
	public long insertEntry(T _myObject)  {
		ContentValues newValues = new ContentValues();
		
		// Assign values for each row.
		for ( String colName : COLUMN_NAMES ) {
			String newValue = getValueForColumn(_myObject, colName);
			if ( newValue != null ) {
				newValues.put( colName, newValue );
			}
		}

		// Insert the row into the table
		return db.insert(DATABASE_TABLE, null, newValues);
	}

	/**
	 * Update an existing object in database by providing an in-memory
	 * instance containing values to update.
	 * 
	 * @param _rowIndex
	 * @param _myObject
	 * @return
	 */
	public boolean updateEntry(long _rowIndex, T _myObject) {
		ContentValues newValues = new ContentValues();
		
		// Assign values for each row.
		for ( String colName : COLUMN_NAMES ) {
			String newValue = getValueForColumn(_myObject, colName);
			if ( newValue != null ) {
				newValues.put( colName, newValue );
			}
		}
		
		String where = KEY_ID + "=" + _rowIndex;
		if ( db.update(DATABASE_TABLE, newValues, where, null) > 0) {
			return true;
		}
		
		return false;
	}

	/**
	 * Remove an object from the database by ID.
	 * 
	 * @param _rowIndex
	 * @return
	 */
	public boolean removeEntry(long _rowIndex) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	}

	/**
	 * Gets a Cursor with all entries in the database.
	 * The caller is responsible for managing the lifecycle of
	 * the returned Cursor.
	 * 
	 * @return
	 */
	public Cursor getAllEntries() {
		return db.query(DATABASE_TABLE, (String[]) COLUMN_NAMES.toArray(), null, null, null, null, null);
	}
	
	/**
	 * Returns all entries in database as a List of T.
	 */
	public List<T> getAllEntriesAsList() {
		List<T> retVal = new ArrayList<T>();
		
		Cursor allEntriesSet = getAllEntries();
		
		try {
			if (allEntriesSet.moveToFirst()) {
				do {
					T objectInstance = null;
					try {
						objectInstance = createObjectFromCursor(allEntriesSet);
					} catch (ClassNotFoundException cnfe) {;}
					
					if ( objectInstance != null ) {
						retVal.add( objectInstance );
					}
				} while ( allEntriesSet.moveToNext() );
			}
		} finally {
			if ( allEntriesSet != null ) {
				allEntriesSet.close();
			}
		}
		
		return retVal;
	}

	/**
	 * Retrieve an object from database by ID.
	 */
	public T getEntry(long _rowIndex) {
		T objectInstance = null;
		Cursor resultSet = null;

		try {
			// Return a cursor to a row from the database
			resultSet = db.query(DATABASE_TABLE,
					 (String[]) COLUMN_NAMES.toArray(),
					 KEY_ID +"=" + Long.toString(_rowIndex),
					 null, null, null, null);

			if (resultSet.moveToFirst()) {
				objectInstance = createObjectFromCursor(resultSet);
			}
		} 
		catch (ClassNotFoundException cnfe) {;}
		finally {
			if (resultSet != null ) {
				resultSet.close();
			}
		}

		return objectInstance;
	}
	
	/**
	 * Given a column name, return the value from the object
	 * that corresponds to the column as a String.
	 * 
	 * @param _myObject
	 * @param colName
	 * @return the value from _myObject corresponding to colName
	 */
	private String getValueForColumn(T _myObject, String colName) {
		String newValue = null;
		
		// Try to obtain _myObject's getter method for this data value
		String methodName = "get" + colName.substring(0,1).toUpperCase() + colName.substring(1, colName.length());
		Method accessorMethod = null;
		try {
			accessorMethod = _myObject.getClass().getMethod( methodName, (Class []) null );
		}
		catch ( NoSuchMethodException nsme ) {;}
		
		// Attempt to fetch value from object and store in ContentValues
		if ( accessorMethod != null ) {
			Type returnType = accessorMethod.getGenericReturnType();
			
			try {
				if (returnType.getClass().equals(String.class)) {
					newValue = (String) accessorMethod.invoke(_myObject, (Object[]) null);
				} else if (returnType.getClass().equals(java.lang.Integer.TYPE)) {
					newValue = ((Integer) accessorMethod.invoke(_myObject, (Object[]) null)).toString();
				}
			} 
			catch (InvocationTargetException ite)  {;}
			catch (IllegalAccessException iae)     {;}
			catch (IllegalArgumentException iarge) {;}
		}

		// TODO Returning NULL for not-found condition rules out storing
		// bona-fide NULL values in database - bad idea?
		return newValue;
	};

	/**
	 * Given a cursor, return a new object populated with the columns
	 * at the current position in the result set.
	 * 
	 * @param resultSet
	 * @return an object of type T
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private T createObjectFromCursor(Cursor resultSet) throws ClassNotFoundException {
		T objectInstance = null;
		
		// Creating an instance of a generic type in Java is unexpectedly difficult...
		// http://stackoverflow.com/questions/75175/create-instance-of-generic-type-in-java
		// So here we cheat by depending on the subclass to tell us what type to create.
		objectInstance = (T) Class.forName( CLASS_NAME );
		
		// Populate all declared columns of the return object from result set.
		for ( String colName : COLUMN_NAMES ) {
			// Get data type of this item via reflection
			String methodName = "set" + colName.substring(0,1).toUpperCase() + colName.substring(1, colName.length());
			Method accessorMethod = null;
			@SuppressWarnings("rawtypes")
			Class returnType = String.class;
			try {
				// First, try a setter that takes a String
				accessorMethod = objectInstance.getClass().getMethod( methodName, returnType );
			}
			catch ( NoSuchMethodException nsme ) {;}
			if ( accessorMethod == null ) {
				// And then try one that takes an integer
				returnType = java.lang.Integer.TYPE;
				try {
					accessorMethod = objectInstance.getClass().getMethod( methodName, returnType );
				}
				catch ( NoSuchMethodException nsme ) {;}
			}

			if ( accessorMethod != null ) {
				try {
					if ( returnType.equals( String.class ) ) {
						// Fetch the value of this item from the DB result set
						String resultValue = resultSet.getString( resultSet.getColumnIndex(colName) );
						// Populate the data member of the return object
						// by invoking it's setter method
						accessorMethod.invoke( objectInstance, resultValue );
					} else {
						Integer resultValue = resultSet.getInt( resultSet.getColumnIndex(colName) );
						accessorMethod.invoke( objectInstance, resultValue );
					}
				}
				catch (InvocationTargetException ite)  {;}
				catch (IllegalAccessException iae)     {;}
				catch (IllegalArgumentException iarge) {;}
			}
		}
		return objectInstance;
	}

}
