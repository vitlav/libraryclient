package org.sergy.libclient.activities;

import org.sergy.libclient.model.Author;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * First, fake search activity. <br/> Real search is in AuthorList activity
 * @author sergy
 *
 */
public class AuthorSearch extends Activity {
	private final static String AUTHOR_KEY = "author";
	
	private EditText fnameEditText;
	private EditText lnameEditText;
	private Author author;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorsearch);
        
        Button searchButton = (Button)findViewById(R.id.search);
        searchButton.setOnClickListener(new SearchButtonListener());
        
        fnameEditText = (EditText)findViewById(R.id.fname);
        lnameEditText = (EditText)findViewById(R.id.lname);
        
        author = savedInstanceState == null ? null : (Author)savedInstanceState.getSerializable(AUTHOR_KEY);
        
        fillFields();
    }

    /**
     * Fills input first name and last name fields using this.author object  
     */
    private void fillFields() {
    	if (author != null) {
        	fnameEditText.setText(author.getFirstName());
        	lnameEditText.setText(author.getLastName());
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putSerializable(AUTHOR_KEY, fillAuthor(null));
    }
    
    /**
     * Fills Author object with data from input fields. <br/>
     * If <code>a</code> is null and at least one field is not empty create new Author object
     * @param a <code>Author</code>
     * @return filled Author object if at least one field is not empty<br/>
     * <code>a</code> in other case
     */
    private Author fillAuthor(Author a) {
    	String fname = fnameEditText.getText().toString();
    	String lname = lnameEditText.getText().toString();
    	
    	if (!fname.equals("") || !lname.equals("")) {
    		if (a == null) {
    			a = new Author();
    		}
    		a.setFirstName(fname);
    		a.setLastName(lname);
    	}
    	return a;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	fillFields();
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Ignore orientation change not to restart activity
		super.onConfigurationChanged(newConfig);
	}
    
    private class SearchButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Author a = fillAuthor(null);
			author = a;
			
			if (a != null) {
				Intent i = new Intent(AuthorSearch.this, AuthorList.class);
		        i.putExtra(AuthorList.AUTHOR_KEY, a);
		        try {
		        	startActivity(i);
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(AuthorSearch.this);
				builder.setMessage(R.string.author_search_dialog)
					.setCancelable(true)
					.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});	
				builder.create().show();
			}
			
		}
    	
    }
}