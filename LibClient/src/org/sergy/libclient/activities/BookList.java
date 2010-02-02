package org.sergy.libclient.activities;

import java.io.IOException;

import org.sergy.libclient.model.Annotation;
import org.sergy.libclient.model.Author;
import org.sergy.libclient.utils.BookDownloader;
import org.sergy.libclient.utils.DBManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity for loading books list for given author and downloading books
 * @author sergy
 *
 */
public class BookList extends AbstractListActivity {
	static final int PROGRESS_DIALOG = 0;

	private Author author;
	private ListView bookList;
	private DBManager dbm;
	private int count;
	private Cursor cursor;
	private Handler handler;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookslist);
		
		author = (Author)getIntent().getSerializableExtra(AuthorList.AUTHOR_KEY);
		handler = new Handler();
		
		dbm = new DBManager(this);
		dbm.open();
		
		if (author != null) {
			TextView header = (TextView)findViewById(R.id.book_header);
			String headerString = author.getFirstName();
			header.setText(headerString);
		}
		
		bookList = (ListView)findViewById(R.id.book_list);
		bookList.setOnItemClickListener(new BookItemClickListener());
		final Annotation annotation = dbm.getAuthorAnnotation(author.getId());
		Button viewButton = (Button)findViewById(R.id.view_button);
		if (annotation.getBody() != null) {
			viewButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(BookList.this, ShowAnnotation.class);
			        i.putExtra(ShowAnnotation.ANNOTATION_KEY, annotation);
			        try {
			        	startActivity(i);
			        } catch (Exception e) {
						Log.e(BookList.class.getSimpleName(), e.getClass() + e.getMessage());
					}
				}
			});
		} else {
			viewButton.setEnabled(false);
		}
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG: //Download progress dialog
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Starting");
			return progressDialog;
		default:
			return null;
		}
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
			LinearLayout layout = (LinearLayout)view;
			TextView format = (TextView)layout.findViewById(R.id.book_format);
			try {
				BookDownloader bookDownloader = new BookDownloader(id, format.getText().toString(), downloadHandler);
				showDialog(BookList.PROGRESS_DIALOG);
				bookDownloader.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// Define the Handler that receives messages from the thread and update the progress
    final Handler downloadHandler = new Handler() {
        public void handleMessage(Message msg) {
        	Bundle b = msg.getData();
            int max = b.getInt(BookDownloader.KEY_SIZE);
            int current = b.getInt(BookDownloader.KEY_CURRENT);
            int state = b.getInt(BookDownloader.KEY_STATE);
            String message = b.getString(BookDownloader.KEY_MESSAGE);
            
            switch (state) {
			case BookDownloader.CONNECTING:
				updateProgressBar(getString(R.string.download_connecting), max, current);
				break;
			case BookDownloader.DOWLOADING:
				updateProgressBar(getString(R.string.download_downloading) + message, max, current);
				break;
			case BookDownloader.FINISHED:
				updateProgressBar(getString(R.string.download_finished) + message, max, current);
				try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				dismissDialog(PROGRESS_DIALOG);
				break;
			case BookDownloader.ERROR:
				updateProgressBar(getString(R.string.download_error) + message, max, current);
				break;
			case BookDownloader.ERROR_BAD_FILE_RETURNED:
				updateProgressBar(getString(R.string.download_error_bad_book) + message, max, current);
				break;
			case BookDownloader.ERROR_RESPONSE_CODE:
				updateProgressBar(getString(R.string.download_error_response) + message, max, current);
				break;
			default:
				break;
			}
        }
        
        private void updateProgressBar(String message, int max, int current) {
        	progressDialog.setMessage(message);
        	progressDialog.setMax(max);
        	progressDialog.setProgress(current);
        }
        
    };

	
}
