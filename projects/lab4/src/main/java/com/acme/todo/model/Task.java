package com.acme.todo.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

@Indexed
public class Task implements Serializable {

	private static final long serialVersionUID = 2315323429163437300L;
	
	@Field(store=Store.YES)
	private Long id;

	@Field(store=Store.YES)
	@Column(length = 100)
	private String title;

	private boolean done;

	private Date createdOn;

	private Date completedOn;

	@IndexedEmbedded(depth=1,prefix="owner_")
	@JsonIgnore
	private User owner;

	public Long getId() {
		return this.id;
	}

	public void setId(final long l) {
		this.id = l;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Task)) {
			return false;
		}
		Task other = (Task) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Date completedOn) {
		this.completedOn = completedOn;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title
				+ ", done=" + done + ", createdOn=" + createdOn
				+ ", completedOn=" + completedOn + ", owner=" + owner + "]";
	}

}