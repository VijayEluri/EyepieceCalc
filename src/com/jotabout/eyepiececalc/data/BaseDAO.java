package com.jotabout.eyepiececalc.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * <p>Interface and base implementation for DAO classes that access the
 * database, including CRUD for model objects.</p>
 * 
 * <p>This class contains methods that move object fields to and from the
 * database in a generic manner.  This is done via reflection to find
 * the getters and setters of the model objects.</p>
 * 
 * <p>DAO subclasses declare the name of the database table they manage,
 * as well as an array naming of the database columns that should be
 * persisted.  The types of the columns are determined by reflection on
 * the model objects.</p>
 * 
 * <p>Limitations, compared to a full ORM tool such as Hibernate:</p>
 * 
 * <ol>
 * <li>Only Strings and integer data types are supported.</li>
 * <li>No support for transparent updates is provided.  Call the DAO
 *     methods to perform CRUD on model objects.</li>
 * <li>No support for building queries, either criteria-based or via
 *     a query language.</li>
 * <li>No support for inheritance or joins is provided.</li>
 * <li>Support for only a primitive migration model exists.</li>
 * </ol>
 * 
 * <p>Starting point was skeleton code from Listing 7-1 of
 * "Professional Android 2 Application Development" by Reto Meier,
 * Wrox Press, 2010.  Heavily developed and revised since then.</p>
 * 
 * @author portuesi
 *
 * @param <T>
 */
public class BaseDAO<T> implements Migration {
	// TODO use old-school counters rather than iterators
	
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
	
	protected final static StringBuilder sb = new StringBuilder();
	
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

		// Register this DAO with the adapter for migrations
		this.dbAdapter.addMigration( this );
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
			String newValue = getValueForColumn( _myObject, colName );
			if ( newValue != null ) {
				newValues.put( colName, newValue );
			}
		}

		// Insert the row into the table
		return db.insert( DATABASE_TABLE, null, newValues );
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
			String newValue = getValueForColumn( _myObject, colName );
			if ( newValue != null ) {
				newValues.put( colName, newValue );
			}
		}
		
		String where = KEY_ID + "=" + _rowIndex;
		if ( db.update(DATABASE_TABLE, newValues, where, null ) > 0) {
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
		sb.setLength(0);

		return db.delete(DATABASE_TABLE,
				sb.append(KEY_ID).append("=").append(_rowIndex).toString(),
				null) > 0;
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
			if ( allEntriesSet.moveToFirst() ) {
				do {
					T objectInstance = null;
					try {
						objectInstance = createObjectFromCursor( allEntriesSet );
					} catch ( ClassNotFoundException cnfe ) {;}
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
			sb.setLength(0);
			
			resultSet = db.query(DATABASE_TABLE,
					 (String[]) COLUMN_NAMES.toArray(),
					 sb.append(KEY_ID).append("=").append(_rowIndex).toString(),
					 null, null, null, null);

			if ( resultSet.moveToFirst() ) {
				objectInstance = createObjectFromCursor( resultSet );
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
	 * Create a database table for this DAO class.
	 */
	@SuppressWarnings("unchecked")
	public void onCreate( SQLiteDatabase _db ) {

		// Craft a SQL statement to create a new database. 
		sb.setLength(0);
		sb.append( "create table " );
		sb.append( DATABASE_TABLE );
		sb.append( " (" );
		sb.append( KEY_ID );
		sb.append( " integer primary key autoincrement, " );
		sb.append( KEY_NAME );
		sb.append( " text not null, " );
		
		// Use reflection to get the column names and data types
		try {
			T objectInstance = (T) Class.forName( CLASS_NAME );

			for ( int i = 0; i < COLUMN_NAMES.size(); i++ ) {
				String colName = COLUMN_NAMES.get(i);
				if ( colName.equals( KEY_ID ) || colName.equals( KEY_NAME ) ) {
					// skip the ID and name fields - already done
					// we assume that KEY_ID and KEY_NAME are at top of list
					continue;
				}

				sb.append( colName );
	
				@SuppressWarnings("rawtypes")
				Class type = getTypeForColumn( objectInstance, colName );
				if ( type.equals( String.class ) ) {
					sb.append( " text" );
				} else if ( type.equals( java.lang.Integer.TYPE )) {
					sb.append( " integer" );
				}
				
				if ( i < COLUMN_NAMES.size() -1 ) {
					sb.append( "," );
				}
				sb.append( " " );
			}
		} catch (ClassNotFoundException ignore) {;}
		
		sb.append(")");
		
		_db.execSQL(sb.toString());
	}

	/**
	 * Upgrade a database table for this DAO class.  This implementation is naive,
	 * and does not manage changing version numbers.  Eventually each subclass will
	 * need to maintain its own version number logic.
	 */
	public void onUpgrade( SQLiteDatabase _db, int _oldVersion, int _newVersion ) {
		// Log the version upgrade.
		Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion + " to " + _newVersion
				+ ", which will destroy all old data");

		// Upgrade the existing database to conform to the new version.
		// Multiple previous versions can be handled by comparing _oldVersion and
		// _newVersion values.

		// The simplest case is to drop the old table and create a new one.
		sb.setLength(0);
		_db.execSQL(sb.append("DROP TABLE IF EXISTS ").append(DATABASE_TABLE).toString());

		// Create a new one.
		onCreate(_db);
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
		String methodName = getterMethodName( colName );
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
					newValue = (String) accessorMethod.invoke( _myObject, (Object[]) null );
				} else if (returnType.getClass().equals( java.lang.Integer.TYPE ) ) {
					newValue = ( (Integer) accessorMethod.invoke( _myObject, (Object[]) null ) ).toString();
				}
			} 
			catch (InvocationTargetException ite)  {;}
			catch (IllegalAccessException iae)     {;}
			catch (IllegalArgumentException iarge) {;}
		}

		// TODO Returning NULL for not-found condition rules out storing
		// bona-fide NULL values in database - is this a bad idea?
		return newValue;
	};
	
	/**
	 * Return the type for a column name.  This implementation tests against
	 * various forms of the setter method on the model object.
	 * 
	 * TODO could probably do this better by listing all methods, then
	 * searching against the method list for a get method, then return its
	 * return type.
	 * 
	 * @param objectInstance
	 * @param colName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Class getTypeForColumn( T objectInstance, String colName ) {
		String methodName = setterMethodName( colName );
		Method accessorMethod = null;
		Class returnType = null;
		try {
			// First, try a setter that takes a String
			accessorMethod = objectInstance.getClass().getMethod( methodName, returnType );
			returnType = String.class;
		}
		catch ( NoSuchMethodException nsme ) {;}
		if ( accessorMethod == null ) {
			// And then try one that takes an integer
			try {
				accessorMethod = objectInstance.getClass().getMethod( methodName, returnType );
				returnType = java.lang.Integer.TYPE;
			}
			catch ( NoSuchMethodException nsme ) {;}
		}
		
		return returnType;
	}

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
			@SuppressWarnings("rawtypes")
			Class returnType = getTypeForColumn( objectInstance, colName );
	
			if ( returnType != null ) {
				try {
					String methodName = setterMethodName( colName );
					Method accessorMethod = objectInstance.getClass().getMethod( methodName, returnType );
					if ( returnType.equals( String.class ) ) {
						// Fetch the value of this item from the DB result set
						String resultValue = resultSet.getString( resultSet.getColumnIndex( colName ) );
						// Populate the data member of the return object
						// by invoking it's setter method
						accessorMethod.invoke( objectInstance, resultValue );
					} else {
						Integer resultValue = resultSet.getInt( resultSet.getColumnIndex( colName ) );
						accessorMethod.invoke( objectInstance, resultValue );
					}
				}
				catch (Exception ignore ) {;}
			}
		}
		return objectInstance;
	}
	
	private String setterMethodName( String colName ) {
		return methodName( colName, "set" );
	}
	
	private String getterMethodName( String colName ) {
		return methodName( colName, "get" );
	}
	
	private String methodName( String colName, String action ) {
		sb.setLength(0);
		return sb.append(action)
				 .append(colName.substring(0,1).toUpperCase())
				 .append(colName.substring(1, colName.length()))
				 .toString();
	}
		
}
