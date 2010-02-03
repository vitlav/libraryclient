package org.sergy.libclient.activities;

import org.sergy.libclient.model.Author;
import org.sergy.libclient.utils.DBManager;

import android.content.Intent;
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

/**
 * Presents author search results
 * @author sergy
 *
 */
public class AuthorList extends AbstractListActivity {
	public static final String AUTHOR_KEY="author";
	private Author searchAuthor;
	private DBManager dbm;
	private ListView authorList;
	
	private Handler handler;
	private int count = 0; //found authors
	private Cursor cursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.authorlist);
			
			handler = new Handler();
			
			authorList = (ListView)findViewById(R.id.author_list);
			authorList.setOnItemClickListener(new AuthorItemClickListener());
			
			searchAuthor = (Author)getIntent().getSerializableExtra(AUTHOR_KEY);
			
			if (searchAuthor != null) {
				TextView header = (TextView)findViewById(R.id.author_header);
				String query = searchAuthor.getFirstName() + " " + searchAuthor.getLastName();
				header.setText(query);
			}
			
			dbm = new DBManager(this);
			dbm.open();
			listPopulated = false;
		} catch (Exception e) {
			Log.e(this.getClass().toString(), e.getClass().toString() + ": " + e.getMessage());
		}	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (searchAuthor == null) {
			createAlertDialog(R.string.no_query_alert).show();
			//if query is empty then nothing to do, finish activity
			finish();
		}
		
		if (!listPopulated) { //if list is empty. Try to search and fill list 
		
			showProgressDialog(R.string.searching);
			
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
									listPopulated = true;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
		        		
					} catch (Exception e) {
						Log.e(AuthorList.this.getClass().toString(), e.getClass() + ": " + e.getMessage());
					}
		        }
			}).start();
		}
	}
	
	/**
	 * Executes author search
	 * @return result count
	 */
	private int search() {
		
		try {
			Cursor cursor = dbm.getAuthors(searchAuthor);
			if (cursor != null) {
		        startManagingCursor(cursor);
		        count = cursor.getCount(); //need this line to force search process
			}
			this.cursor = cursor;
		} catch (Exception e) {
			Log.e(this.getClass().toString(), e.getClass().toString() + ": " + e.getMessage());
		}
		
        return count;
	}
	
	/**
	 * Shows result count in UI
	 */
	private void setResultsCount() {
		TextView resultsCount = (TextView)findViewById(R.id.results_count);
		resultsCount.setText(String.valueOf(count));
	}
	
	/**
	 * Fills result list in UI
	 */
	private void setListRows() {
		Cursor cursor = this.cursor;
		
		if (cursor != null) { //if have search results
			 //Fields from cursor
	        String[] from = new String[]{DBManager.AUTHOR_SEARCH_NAME};
	        
	        //UI elements to bind
	        int[] to = new int[]{R.id.author_name};
	        
			 //Create a simple cursor adapter
	        SimpleCursorAdapter rows = new SimpleCursorAdapter(this, R.layout.authorsrow, cursor, from, to);
			authorList.setAdapter(rows);
		}
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
	
	private class AuthorItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(AuthorList.this, BookList.class);
			LinearLayout layout = (LinearLayout)view;
			TextView textView = (TextView)layout.getChildAt(0);
			Author author = new Author();
			author.setId(id);
			author.setFirstName(textView != null ? textView.getText().toString() : "");
	        i.putExtra(AuthorList.AUTHOR_KEY, author);
	        startActivity(i);
			
		}
		
	}
}
