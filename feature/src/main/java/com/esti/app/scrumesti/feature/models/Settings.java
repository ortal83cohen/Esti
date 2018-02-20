package com.esti.app.scrumesti.feature.models;

/**
 * Created by Ortal Cohen on 19/2/2018.
 */

public class Settings {

	int complexityWeight;
	int clarityWeight;
	int dependencyWeight;

	public Settings() {
		this.complexityWeight = 60;
		this.clarityWeight = 20;
		this.dependencyWeight = 20;
	}
	public Settings(int complexityWeight, int clarityWeight, int dependencyWeight) {
		this.complexityWeight = complexityWeight;
		this.clarityWeight = clarityWeight;
		this.dependencyWeight = dependencyWeight;
	}

	public int getComplexityWeight() {
		return complexityWeight;
	}

	public int getClarityWeight() {
		return clarityWeight;
	}

	public int getDependencyWeight() {
		return dependencyWeight;
	}


	public int getTotalWeight() {
		return dependencyWeight + clarityWeight + complexityWeight;
	}


}
