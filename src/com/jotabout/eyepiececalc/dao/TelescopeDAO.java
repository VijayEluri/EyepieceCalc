package com.jotabout.eyepiececalc.dao;

import com.jotabout.eyepiececalc.data.BaseDAO;
import com.jotabout.eyepiececalc.data.DBAdapter;
import com.jotabout.eyepiececalc.model.Telescope;

/**
 * DAO class for performing CRUD with telescopes.
 * 
 * @author portuesi
 *
 */
public class TelescopeDAO extends BaseDAO<Telescope> {
	
	// The names of each database column unique to this object.
	protected String KEY_APERTURE = "aperture";
	protected String KEY_FOCAL_LENGTH = "focalLength";

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
