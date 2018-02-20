package com.esti.app.scrumesti.feature.models;

/**
 * Created by Ortal Cohen on 13/2/2018.
 */

public class User {

	String name;
	int complexity;
	int clarity;
	int dependency;
	boolean isMe;

	public User(String name, int complexity, int clarity, int dependency,boolean isMe) {
		this.name = name;
		this.complexity = complexity;
		this.clarity = clarity;
		this.dependency = dependency;
		this.isMe = isMe;
	}

	public int getClarity() {
		return clarity;
	}

	public void setClarity(int clarity) {
		this.clarity = clarity;
	}

	public boolean isMe() {
		return isMe;
	}

	public void setMe(boolean me) {
		isMe = me;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public int getclarity() {
		return clarity;
	}

	public void setclarity(int clarity) {
		this.clarity = clarity;
	}

	public int getDependency() {
		return dependency;
	}

	public void setDependency(int dependency) {
		this.dependency = dependency;
	}
}
