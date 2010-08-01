package com.jotabout.eyepiececalc.model;

/**
 * Model class describing a telescope.
 * 
 * @author portuesi
 *
 * TODO add calculations either here, or on the eyepiece class.
 * Or else a Combinations model object.
 *
 */
public class Telescope {
	
	private int id;
	private String name;
	private int focalLength;
	private int aperture;

	public Telescope() {
		super();
	}

	public Telescope(int id, String name, int focalLength, int aperture) {
		super();
		this.id = id;
		this.name = name;
		this.focalLength = focalLength;
		this.aperture = aperture;
	}
	
	/**
	 * Database id
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Telescope name
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Telescope focal length, in millimeters.
	 * @return
	 */
	public int getFocalLength() {
		return focalLength;
	}
	
	public void setFocalLength(int focalLength) {
		this.focalLength = focalLength;
	}
	
	/**
	 * Telescope aperture, in millimeters.
	 * @return
	 */
	public int getAperture() {
		return aperture;
	}
	
	public void setAperture(int aperture) {
		this.aperture = aperture;
	}

}
