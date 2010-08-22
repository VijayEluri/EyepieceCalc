package com.jotabout.eyepiececalc.test.data;

import junit.framework.Assert;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import com.jotabout.eyepiececalc.EyepieceCalc;
import com.jotabout.eyepiececalc.data.DBAdapter;
import com.jotabout.eyepiececalc.data.Migration;

public class DBAdapterTest extends ActivityInstrumentationTestCase2<EyepieceCalc> {

	public static final String DATABASE_FILE = "epDatabase.db";
	public static final String DATABASE_TABLE_1 = "mainTable";
	public static final String DATABASE_TABLE_2 = "otherTable";
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "name";

	private DBAdapter dba;
	private Context context;

	public DBAdapterTest() {
		super("com.jotabout.eyepiececalc", EyepieceCalc.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		context = getInstrumentation().getTargetContext().getApplicationContext();
		deleteDatabase();

		dba = new DBAdapter( context );
		dba.addMigration( new TestMigration( DATABASE_TABLE_1 ) );
	}

	@Override
	protected void tearDown() throws Exception {
		dba.close();
		dba = null;
		deleteDatabase();

		super.tearDown();
	}
	
	public void testPreconditions() {
		Assert.assertNotNull( context );
		Assert.assertEquals( 0, context.databaseList().length );
		Assert.assertNotNull( dba );
	}
	
	public void testOpen() {
		dba.open();
		dba.close();

	}
	
	public void testClose() {
		dba.open();
		dba.close();

	}
	
	public void testGetDatabase() {
		dba.open();
		assertNotNull( dba.getDatabase() );
		assertNotNull( dba.getDatabase().getPath() );
		dba.close();

	}
	
	public void testAddMigration() {
		dba.addMigration( new TestMigration( DATABASE_TABLE_2 ) );
		dba.open();
		dba.close();

	}
	
	public void testCreateMigrations() {
		dba.addMigration( new TestMigration( DATABASE_TABLE_2 ) );
		dba.open();
		dba.close();
	
		// verify table was created

	}
	
	public void testUpgradeMigrations() {
		dba.addMigration( new TestMigration( DATABASE_TABLE_2 ) );
		dba.open();
		dba.close();
	
		// verify tables were upgraded

	}
	
	public boolean deleteDatabase() {
		boolean success = context.deleteDatabase( DATABASE_FILE );
		return success;
	}
	
	private class TestMigration implements Migration {
		
		private String databaseTable;
		
		public TestMigration(String databaseTable) {
			super();
			this.databaseTable = databaseTable;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			String dataBaseCreateSQL = "create table " + databaseTable + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME + " text not null);";
			
			db.execSQL(dataBaseCreateSQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Assert.assertTrue(true);
		}
	}

}
