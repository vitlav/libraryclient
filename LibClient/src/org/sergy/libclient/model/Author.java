package org.sergy.libclient.model;

import java.io.Serializable;

public class Author implements Serializable{
	private static final long serialVersionUID = -8030468903510086885L;
	
	private long id;
	private String firstName;
	private String lastName;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
}
