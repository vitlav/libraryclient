package org.sergy.libclient.model;

import java.io.Serializable;

public class Annotation implements Serializable {
	private static final long serialVersionUID = -650816107871538189L;
	
	private String title;
	private String body;
	private String pic;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	

}
