package com.jotabout.eyepiececalc.model;

/**
 * Model class describing a telescope.
 * 
 * @author portuesi
 *
 */
public class Eyepiece {
	
	private long id;
	private String name;
	private int focalLength;
	private int apparentFOV;
	
	public Eyepiece() {
		super();
	}

	public Eyepiece(long id, String name, int focalLength, int apparentFOV) {
		super();
		this.id = id;
		this.name = name;
		this.focalLength = focalLength;
		this.apparentFOV = apparentFOV;
	}

	/**
	 * Database ID.
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Eyepiece name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Eyepiece focal length, in millimeters.
	 * 
	 * @return
	 */
	public int getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(int focalLength) {
		this.focalLength = focalLength;
	}

	/**
	 * Eyepiece apparent FOV, in degrees.  This is determined by the optical
	 * design of the eyepiece.
	 * 
	 * @return
	 */
	public int getApparentFOV() {
		return apparentFOV;
	}

	public void setApparentFOV(int apparentFOV) {
		this.apparentFOV = apparentFOV;
	}

}
