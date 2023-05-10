package model;

import java.io.Serializable;

public class Student extends Person implements Serializable {
	private double gpa;
	private String major;

	public Student(Name name, double gpa, String major) {
		super(name);
		this.gpa = gpa;
		this.major = major;
	}

	public double getGpa() {
		return gpa;
	}

	public void setGpa(double gpa) {
		this.gpa = gpa;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	@Override
	public String toString() {
		return "Student " + super.toString() + ", [GPA = " + gpa + "], [Major = " + major + "]";
	}

}
