package org.sergy.libclient.activities;

import org.sergy.libclient.model.Annotation;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

public class ShowAnnotation extends Activity {
	public static final String ANNOTATION_KEY = "annotation";
	private Annotation annotation;
	private ImageView pic;
	private WebView webView;
	
	final String mimetype = "text/html";
	final String encoding = "UTF-8";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showannotation);
		
		annotation = (Annotation)getIntent().getSerializableExtra(ANNOTATION_KEY);
		pic = (ImageView)findViewById(R.id.pic);
		webView = (WebView)findViewById(R.id.web_annotation);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		String body = annotation.getBody();
		if (body != null) {
			body = body.replace("\\r\\n", "<br/>").replace("\\\"","\"");
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
