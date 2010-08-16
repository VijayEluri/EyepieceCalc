package com.jotabout.eyepiececalc.dao;

import com.jotabout.eyepiececalc.data.BaseDAO;
import com.jotabout.eyepiececalc.data.DBAdapter;
import com.jotabout.eyepiececalc.model.Eyepiece;

/**
 * DAO class for performing CRUD with eyepieces.
 * 
 * @author portuesi
 *
 */
public class EyepieceDAO extends BaseDAO<Eyepiece> {

	// The names of each database column unique to this object.
	protected String KEY_AFOV = "apparentFOV";
	protected String KEY_FOCAL_LENGTH = "focalLength";

	public EyepieceDAO(DBAdapter dbAdapter) {
		super(dbAdapter);
		
		// Declare class we manage
		CLASS_NAME = "com.jotabout.eyepiececalc.model.Eyepiece";

		// Declare database table and columns
		DATABASE_TABLE = "eyepieces";

		COLUMN_NAMES.add( KEY_AFOV );
		COLUMN_NAMES.add( KEY_FOCAL_LENGTH );
	}

}
