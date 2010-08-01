package com.jotabout.eyepiececalc.data;

import com.jotabout.eyepiececalc.model.Telescope;

/**
 * DAO class for performing CRUD with telescopes.
 * 
 * @author portuesi
 *
 */
public class TelescopeDAO extends BaseDAO<Telescope> {
	
	// The name and column index of each database column unique to this object.
	protected String KEY_APERTURE = "aperture";
	protected int APERTURE_COLUMN = 2;
	
	protected String KEY_FOCAL_LENGTH = "focalLength";
	protected int FOCAL_LENGTH_COLUMN = 3;

	public TelescopeDAO(DBAdapter dbAdapter) {
		super(dbAdapter);
		
		// Declare class we manage
		CLASS_NAME = "com.jotabout.eyepiececalc.model.Telescope";
		
		// Declare database table and columns
		DATABASE_TABLE = "telescopes";

		COLUMN_NAMES.add( KEY_APERTURE );
		COLUMN_NAMES.add( KEY_FOCAL_LENGTH );
	}

}
