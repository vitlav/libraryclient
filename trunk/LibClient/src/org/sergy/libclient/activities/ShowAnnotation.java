package org.sergy.libclient.activities;

import java.io.IOException;

import org.sergy.libclient.model.Annotation;
import org.sergy.libclient.utils.ZipReader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

public class ShowAnnotation extends Activity {
	public static final String ANNOTATION_KEY = "annotation";
	private Annotation annotation;
	private ImageView pic;
	private WebView webView;
	
	final String mimetype = "text/html";
	final String encoding = "UTF-8";
	private Button button;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showannotation);
		
		annotation = (Annotation)getIntent().getSerializableExtra(ANNOTATION_KEY);
		pic = (ImageView)findViewById(R.id.pic);
		pic.setVisibility(View.GONE);
		webView = (WebView)findViewById(R.id.web_annotation);
		button = (Button)findViewById(R.id.image_on_off);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (pic.getVisibility() == View.GONE) {
					pic.setVisibility(View.VISIBLE);
				} else {
					pic.setVisibility(View.GONE);
				}
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		String picName = annotation.getPic(); 
		if (picName != null && !"".equals(picName)) {
			try {
				Bitmap bitmap = ZipReader.getBitmap(picName);
				pic.setImageBitmap(bitmap);
			} catch (IOException e) {
				Log.e(this.getClass().getSimpleName(), e.getClass() + e.getMessage());
			}
		} else {
			button.setVisibility(View.GONE);
		}
		
		String body = annotation.getBody();
		if (body != null) {
			body = body.replace("\\r\\n", "<br/>").replace("\\n\\n", "<br/>").replace("\\n", "<br/>").replace("\\\"","\"");
			body = body.replace("[b]", "<b>").replace("[/b]", "</b>");
		} else {
			body = getString(R.string.annotation_not_found);
		}
		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append(body);
		sb.append("</body></html>");
		webView.loadData(sb.toString(),
	               mimetype,
	               encoding);

	}
}
