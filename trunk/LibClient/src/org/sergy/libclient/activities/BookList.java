package org.sergy.libclient.activities;

import java.io.IOException;

import org.sergy.libclient.model.Author;
import org.sergy.libclient.utils.BookDownloader;
import org.sergy.libclient.utils.DBManager;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BookList extends AbstractListActivity {
	private Author author;
	private ListView bookList;
	private DBManager dbm;
	private int count;
	private Cursor cursor;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookslist);
		
		author = (Author)getIntent().getSerializableExtra(AuthorList.AUTHOR_KEY);
		handler = new Handler();
		
		if (author != null) {
			TextView header = (TextView)findViewById(R.id.book_header);
			String headerString = author.getFirstName();
			header.setText(headerString);
		}
		
		bookList = (ListView)findViewById(R.id.book_list);
		bookList.setOnItemClickListener(new BookItemClickListener());
		
		dbm = new DBManager(this);
		dbm.open();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (author == null) {
			createAlertDialog(R.string.no_author).show();
			//if query is empty then nothing to do, finish activity
			finish();
			return;
		}
		
		showProgressDialog(R.string.loading);
		
		//Start searching
		new Thread(new Runnable() {
	        @Override
	        public void run() {
	        	try {
	        		search();
	        		
	        		//Using Handler for correct work of UI elements
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								setListRows();
								setResultsCount();
								hideProgressDialog();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
	        		
				} catch (Exception e) {
					Log.e(BookList.this.getClass().toString(), e.getClass() + ": " + e.getMessage());
				}
	        }
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbm.close();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Ignore orientation change not to restart activity
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * Set books count in IU. Invoke after search() method
	 */
	private void setResultsCount() {
		TextView resultsCount = (TextView)findViewById(R.id.books_count);
		resultsCount.setText(String.valueOf(count));
	}
	
	private int search() {
		try {
			Cursor cursor = dbm.getBooksByAuthorId(author.getId());
			if (cursor != null) {
		        startManagingCursor(cursor);
		        count = cursor.getCount();
			}
			this.cursor = cursor;
		} catch (Exception e) {
			Log.e(this.getClass().toString(), e.getClass().toString() + ": " + e.getMessage());
		}
		
        return count;
	}

	/**
	 * load results to ListView
	 */
	private void setListRows() {
		Cursor cursor = this.cursor;
		
		if (cursor != null) { //if have search results
			 //Fields from cursor
	        String[] from = new String[]{DBManager.BOOK_SEARCH_TITLE,
	        												DBManager.BOOK_SEARCH_LANG,
	        												DBManager.BOOK_SEARCH_FORMAT,
	        												DBManager.BOOK_SEARCH_SIZE
	        												};
	        
	        //UI elements to bind
	        int[] to = new int[]{R.id.book_title,
	        									R.id.book_lang,
	        									R.id.book_format,
	        									R.id.book_size
	        									};
	        
			 //Create a simple cursor adapter
	        SimpleCursorAdapter rows = new SimpleCursorAdapter(this, R.layout.booklistrow, cursor, from, to);
			bookList.setAdapter(rows);
		}
	}
	
	private class BookItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			BookDownloader bookDownloader = new BookDownloader();
			LinearLayout layout = (LinearLayout)view;
			TextView format = (TextView)layout.findViewById(R.id.book_format);
			try {
				bookDownloader.downloadBook(id, format.getText().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
