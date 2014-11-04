package com.acme.todo.model;

import java.io.Serializable;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;


public class User implements Serializable {

	private static final long serialVersionUID = -5624201782761601738L;

	@Field(store=Store.YES)
	private String username = null;

	public User() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (username != null)
			result += "username: " + username;
		return result;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		if (username != null) {
			return username.equals(((User) that).username);
		}
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		if (username != null) {
			return username.hashCode();
		}
		return super.hashCode();
	}

	
}