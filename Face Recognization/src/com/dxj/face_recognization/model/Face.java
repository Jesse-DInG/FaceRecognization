package com.dxj.face_recognization.model;

import org.opencv.core.Rect;

public class Face {
	public Rect  face_area=new Rect(); 
    public Rect  left_eye=new Rect();
    public Rect  right_eye=new Rect();
    public float  scale=0.0f;
    public boolean enable=false;
}
