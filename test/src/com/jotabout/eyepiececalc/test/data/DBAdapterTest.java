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
		resetMigrationCounts();
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
		
		SQLiteDatabase db = dba.getDatabase();
		Assert.assertNotNull( db );
		Assert.assertTrue( db.isOpen() );
		Assert.assertTrue( db.getPath().contains(DATABASE_FILE) );
		Assert.assertTrue( db.getVersion() == 1 );
		
		Assert.assertEquals( 1, createCount);
		Assert.assertEquals( 0, upgradeCount);
		
		dba.close();
	}
	
	public void testClose() {
		dba.open();
		dba.close();

		Assert.assertNull( dba.getDatabase() );
	}
	
	public void testAddMigration() {
		dba.addMigration( new TestMigration( DATABASE_TABLE_2 ) );
		dba.open();
		dba.close();

		Assert.assertEquals( 2, createCount);
		Assert.assertEquals( 0, upgradeCount);
	}
	
	public void testCreateMigrations() {
		dba.addMigration( new TestMigration( DATABASE_TABLE_2 ) );
		dba.open();
		dba.close();
	
		Assert.assertEquals( 2, createCount);
		Assert.assertEquals( 0, upgradeCount);

		// Verify tables were created
		SQLiteDatabase db = dba.open().getDatabase();
		db.execSQL( "select * from " + DATABASE_TABLE_1 );
		db.execSQL( "select * from " + DATABASE_TABLE_2 );
	}
	
	public void testUpgradeMigrations() {
		dba.addMigration( new TestMigration( DATABASE_TABLE_2 ) );
		dba.open();
		dba.close();

		// Verify creates were called
		Assert.assertEquals( 2, createCount );
		Assert.assertEquals( 0, upgradeCount );
		SQLiteDatabase db = dba.open().getDatabase();
		db.execSQL( "select * from " + DATABASE_TABLE_1 );
		db.execSQL( "select * from " + DATABASE_TABLE_2 );
		Assert.assertEquals(1, db.getVersion() );
		
		// HACK - need a better way to update database version
		DBAdapter.DATABASE_VERSION = 2;
		dba = new DBAdapter( context );
		dba.addMigration( new TestMigration( DATABASE_TABLE_1 ) );
		dba.addMigration( new TestMigration( DATABASE_TABLE_2 ) );
		dba.open();
		dba.close();
	
		// Verify upgrades were called
		Assert.assertEquals( 2, createCount );
		Assert.assertEquals( "upgrade must be called twice", 2, upgradeCount );
		db = dba.open().getDatabase();
		db.execSQL( "select * from " + DATABASE_TABLE_1 );
		db.execSQL( "select * from " + DATABASE_TABLE_2 );
		Assert.assertEquals( "database version must be 2", 2, db.getVersion() );
	}
	
	public boolean deleteDatabase() {
		boolean success = context.deleteDatabase( DATABASE_FILE );
		return success;
	}
	
	public int createCount = 0;
	public int upgradeCount = 0;
	
	public void resetMigrationCounts()
	{
		createCount = 0;
		upgradeCount = 0;
	}

	/**
	 * A sample migration class for driving the tests.
	 * 
	 * @author portuesi
	 *
	 */
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
			
			createCount++;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Assert.assertNotSame("version numbers must be different", oldVersion, newVersion );
			upgradeCount++;
		}
	}

}
