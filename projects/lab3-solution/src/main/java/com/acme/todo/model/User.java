package com.acme.todo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed
public class User implements Serializable {

	private static final long serialVersionUID = -5624201782761601738L;

	
	@Field(store=Store.YES)
	@Id
	@Column(name = "username", updatable = false, nullable = false)
	private String username = null;
	
	@Field(store=Store.YES)
	@Column
	private String email = null;
	
	@Version
	@Column(name = "version")
	private int version = 0;
	
//	@IndexedEmbedded(prefix="tasks.task",depth=1)
	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy="owner")
	private List<Task> tasks = new ArrayList<Task>();

	
	
	public User() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
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

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	
}