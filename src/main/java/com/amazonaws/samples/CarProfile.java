package com.amazonaws.samples;

public class CarProfile {
	private String year;
	private String make;
	private String model;
	private String name;
	private String insta;
	private String throttle;
	private String injectors;
	private String pulley;
	private String headers; 
	private String frontpipe;
	private String catback;
	private String diameter;
	
	public CarProfile() {
		
	}
	
	public CarProfile(String year, String make, String model, String name, String insta, String throttle, String injectors, String pulley, String headers, String frontpipe, String catback, String diameter){
		this.year = year;
		this.make = make;
		this.model = model;
		this.name = name;
		this.insta = insta;
		this.throttle = throttle;
		this.injectors = injectors;
		this.pulley = pulley;
		this.headers = headers;
		this.frontpipe = frontpipe;
		this.catback = catback;
		this.diameter = diameter;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInsta() {
		return insta;
	}

	public void setInsta(String insta) {
		this.insta = insta;
	}

	public String getThrottle() {
		return throttle;
	}

	public void setThrottle(String throttle) {
		this.throttle = throttle;
	}

	public String getInjectors() {
		return injectors;
	}

	public void setInjectors(String injectors) {
		this.injectors = injectors;
	}

	public String getPulley() {
		return pulley;
	}

	public void setPulley(String pulley) {
		this.pulley = pulley;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public String getFrontpipe() {
		return frontpipe;
	}

	public void setFrontpipe(String frontpipe) {
		this.frontpipe = frontpipe;
	}

	public String getCatback() {
		return catback;
	}

	public void setCatback(String catback) {
		this.catback = catback;
	}
	
	public String getDiameter() {
		return diameter;
	}

	public void setDiameter(String diameter) {
		this.diameter = diameter;
	}
	
	public String getAllInfo() {
		String info = "<"+year+"!"+make+"?"+model+"#"+name+"$"+insta+"%"+throttle+"^"+injectors+"&"+pulley+"*"+headers+"("+frontpipe+")"+catback+"+"+diameter+">";
		return info;
	}

}
