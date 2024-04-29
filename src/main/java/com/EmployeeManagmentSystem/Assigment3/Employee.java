package com.EmployeeManagmentSystem.Assigment3;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String firstName;
    private String lastName;
    private String employeeID;
    private String designation;
    private List<Language> knownLanguages;


    public Employee(){
        this.employeeID = "0";
        this.firstName = "";
        this.designation = "";
        this.lastName = "";
        this.knownLanguages = new ArrayList<>();
    }

    public Employee(String employeeID, String firstName, String lastName, String designation, List<Language> knownLanguages) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.designation = designation;
        this.lastName = lastName;
        this.knownLanguages = knownLanguages;
    }

    public Employee(Employee newEmployee) {
        setFirstName(newEmployee.getFirstName());
        setDesignation(newEmployee.getDesignation());
        setLastName(newEmployee.getLastName());
        setKnownLanguages(newEmployee.getKnownLanguages());
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setKnownLanguages(List<Language> knownLanguages) {
        this.knownLanguages = knownLanguages;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Language> getKnownLanguages() {
        return knownLanguages;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getDesignation() {
        return designation;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


}
