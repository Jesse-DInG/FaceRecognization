package com.dxj.face_recognization.model;

public class Faceinfo {
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int[] getLbp() {
		return lbp;
	}
	public void setLbp(int[] lbp) {
		this.lbp = lbp;
	}
	public float[] getWeight() {
		return weight;
	}
	public void setWeight(float[] weight) {
		this.weight = weight;
	}
	private int id;
	private String name;
	private int[] lbp;
	private float[] weight;
}
