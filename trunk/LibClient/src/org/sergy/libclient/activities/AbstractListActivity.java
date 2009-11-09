package org.sergy.libclient.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public abstract class AbstractListActivity extends Activity {
	private ProgressDialog progressDialog;
	
	protected void showProgressDialog(int message) {
		progressDialog = ProgressDialog.show(this, "", getString(message), true);
	}
	
	protected void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * Creates AlertDialog with one OK button
	 * @param message
	 * @return
	 */
	protected AlertDialog createAlertDialog(int message) {
		//Create AlertDialod
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
			.setCancelable(true)
			.setMessage(message)
			.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
		return builder.create();
	}

}
