package org.openforis.collect.android.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.AndroidSQLiteDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.logging.LogFactory;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.liquibase.AndroidLiquibaseLogger;
import org.openforis.collect.android.service.ServiceFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;

/**
 * 
 * @author K. Waga
 * @author S. Ricci
 *
 */
public abstract class DatabaseHelper {
	
	private static final String LIQUIBASE_CHANGELOG = "org/openforis/collect/db/changelog/db.changelog-master.xml";
	public static final String DB_NAME = "collect.db";
	public static final String DB_PATH = "/data/data/org.openforis.collect.android/databases/";
	
	private static Context contex;
	private static Configuration config;
	
	
	
	public static SQLiteDatabase getDb() {
		OpenHelper openHelper = new OpenHelper(contex, config);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		return db;
	}
	
	public static void init(Context ctx, Configuration config){
		contex = ctx;
		DatabaseHelper.config = config;
		/*try {
			DatabaseHelper.copyDataBase(null);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		createDatabase(ctx, config);
	}

	private static void createDatabase(Context ctx, Configuration config) {
			OpenHelper openHelper = new OpenHelper(ctx, config);
			SQLiteDatabase db = openHelper.getWritableDatabase();
	       	try{
	       		if ( db == null ) {
	       		throw new RuntimeException("Null db");
	       	}
	       	db.close();
       	}catch(Exception e){
       		e.printStackTrace();
       	}finally{
       		db.close();
       	}
	}

	public static void updateDBSchema() {
		Connection c = null;
		Database database = new AndroidSQLiteDatabase();
		try {
			c = ServiceFactory.getDataSource().getConnection();
			LogFactory.putLogger(new AndroidLiquibaseLogger());
			database.setConnection(new JdbcConnection(c));
			Liquibase liquibase = new Liquibase(LIQUIBASE_CHANGELOG, 
					new ClassLoaderResourceAccessor(), database);
			liquibase.update(null);
			//database.close();
//		    c.close();
		} catch(Exception e) {
			if (c != null) {
                try {
					c.rollback();
				} catch (SQLException e1) {}
	        }
			throw new RuntimeException(e);
		} finally {
			/*if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {}
			}*/
			try {
				database.close();
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void closeConnection() {
		SQLDroidDataSource dataSource = ServiceFactory.getDataSource();
		try {
			Connection c = dataSource.getConnection(false);
			if ( c != null && ! c.isClosed() ) {
				c.close();
			}
		} catch(Exception e) {}
	}

	private static class OpenHelper extends SQLiteOpenHelper {
		OpenHelper(Context context, Configuration config) {
			super(context, config.getDbName(), null, config.getDbVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	
	public static void copyDataBase(String pathToFileOnSdcard) throws IOException{
		//Open your local db as the input stream
		InputStream myInput;
		if (pathToFileOnSdcard!=null){
			myInput = new FileInputStream(pathToFileOnSdcard);
		} else {
			myInput = contex.getAssets().open(DB_NAME);
		}
		
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		
		File file = new File(outFileName);
		//if(!file.exists()){
			String dirPath = DB_PATH;
			File projDir = new File(dirPath);
			if (!projDir.exists()){
				projDir.mkdirs();
			}
			    
			//Open the empty db as the output stream
			if (!file.exists())
				file.createNewFile();
			OutputStream myOutput = new FileOutputStream(outFileName);
			
			//transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}
			
			//Close the streams
			myOutput.flush();
			myOutput.close();
		//}
		 

		myInput.close();
		 
		}
	
	public static void backupDatabase(String pathToDestinationFolderOnSdcard, String destFileName) throws IOException{		
		String dbFileName = DB_PATH + DB_NAME;		
		File file = new File(dbFileName);
		if(file.exists()){
			InputStream databaseFileStream = new FileInputStream(dbFileName);
			
			OutputStream destinationFileStream = new FileOutputStream(pathToDestinationFolderOnSdcard+destFileName);
			
			//transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = databaseFileStream.read(buffer))>0){
				destinationFileStream.write(buffer, 0, length);
			}
			
			//Close the streams
			destinationFileStream.flush();
			destinationFileStream.close();
			databaseFileStream.close();
		}
		
		
	}
}