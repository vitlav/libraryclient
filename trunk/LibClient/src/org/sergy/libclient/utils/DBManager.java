package org.sergy.libclient.utils;

import org.sergy.libclient.activities.R;
import org.sergy.libclient.model.Annotation;
import org.sergy.libclient.model.Author;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DBManager {

	//Public keys
	public static final String AUTHOR_SEARCH_NAME = "name";
	public static final String AUTHOR_SEARCH_ID = "_id";
	
	public static final String BOOK_SEARCH_ID = "_id";
	public static final String BOOK_SEARCH_SIZE = "size";
	public static final String BOOK_SEARCH_TITLE = "title";
	public static final String BOOK_SEARCH_LANG = "lang";
	public static final String BOOK_SEARCH_FORMAT = "format";

	//Queries
	private static final String AUTHOR_SEARCH_QUERY = "select (LastName || ' ' || FirstName || ' ' || MiddleName) as " + AUTHOR_SEARCH_NAME + ", AvtorId as " + AUTHOR_SEARCH_ID + " from libavtorname an where FirstName like ? and LastName like ? and (select count(*) from libbook b join libavtor a on b.BookId=a.BookId where a.AvtorId=an.AvtorId) > 0 limit ";
	private static final String BOOK_SEARCH_BY_AUTHOR_ID_QUERY = "select b.BookId " + BOOK_SEARCH_ID + ", b.FileSize " + BOOK_SEARCH_SIZE + ", b.Title " + BOOK_SEARCH_TITLE + ", b.Lang " + BOOK_SEARCH_LANG + ", b.FileType " + BOOK_SEARCH_FORMAT + " from libbook b join libavtor a on b.BookId=a.BookId where b.Deleted<>1 and b.Blocked<>1 and b.Broken<>1 and a.AvtorId=";
	
	
    private SQLiteDatabase mDb;
    private Context ctx;

    private static final String DATABASE_PATH = Environment.getExternalStorageDirectory() + "/lib.db";


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public  DBManager(Context ctx) {
    	this.ctx = ctx;
    }

    /**
     * Open the lib database.
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be opened
     */
    public DBManager open() throws SQLException {
        mDb = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return this;
    }
    
    public void close() {
        mDb.close();
    }

    /**
     * Search authors by Author.FirstName and Author.LastName<br/>
     * this query have maximum result LIMIT
     * @param author filled <code>Author</code> object for search
     * @return 
     */
    public Cursor getAuthors(Author author) {
    	try {
    		if (author != null) {
    			//TODO Change AUTHOR_SEARCH_QUERY. Don't use AUTHOR_SEARCH_QUERY + ctx.getString(R.string.author_search_limit)
    			Cursor cursor = mDb.rawQuery(AUTHOR_SEARCH_QUERY + ctx.getString(R.string.author_search_limit), new String[] {prepareLIKECondition(author.getFirstName()), prepareLIKECondition(author.getLastName())});
    			return cursor;
    		}
    	} catch (Exception e) {
    		Log.e(this.getClass().toString(), e.getClass().toString() + ": " + e.getMessage());
		}
    	
    	return null;
    	
    }
    
    public Cursor getBooksByAuthorId(long authorId) {
    	String query = BOOK_SEARCH_BY_AUTHOR_ID_QUERY + String.valueOf(authorId);
    	try {
    		Cursor cursor = mDb.rawQuery(query, null);
    		return cursor;
    	} catch (Exception e) {
    		Log.e(this.getClass().toString(), e.getClass().toString() + ": " + e.getMessage());
		}
    	return null;
    }
    
    public Annotation getAuthorAnnotation(long authorId) {
    	Annotation result = new Annotation();
    	String query = "select a.title, a.Body, p.File from libaannotations a left join libapics p on a.AvtorId=p.AvtorId where a.AvtorId=";
    	try {
	    	Cursor cursor = mDb.rawQuery(query + String.valueOf(authorId), null);
	    	if (cursor.moveToFirst()) {
	    		result.setTitle(cursor.getString(0));
	    		result.setBody(cursor.getString(1));
	    		result.setPic(cursor.getString(2));
	    	}
	    	cursor.close();
    	} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getClass() + e.getMessage());
		}
    	return result;
    }
    
    
    public int getBooksCount() {
    	int result = -1;
    	
    	String sql = "select count(*) from libavtor";
    	
    	Cursor cursor = mDb.rawQuery(sql, null);
    	
    	if (cursor.moveToFirst()) {
    		result = cursor.getInt(0);
    	}
    	return result;
    }
    
    
    /**
     * Prepares string for using in LIKE sql condition
     * @param str
     * @return
     */
    private String prepareLIKECondition(String str) {
    	String result = "%";
    	
    	if (str != null && !"".equals(str)) {
    		result += str.trim().replace('*', '%').replace("'", "''") + "%"; 
    	}
    	
    	return result;
    }

}
