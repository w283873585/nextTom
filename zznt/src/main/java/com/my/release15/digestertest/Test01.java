package com.my.release15.digestertest;

import java.io.File;

import org.apache.commons.digester.Digester;

public class Test01 {
	public static void main(String[] args) {
		Digester digester = new Digester();
		String path = Test01.class.getClassLoader().getResource("").getPath();
	    File file = new File(path, "E.xml");
		
	    digester.addObjectCreate("employee", Employee.class);
	    digester.addSetProperties("employee");
	    digester.addCallMethod("employee", "printName");
	    digester.addObjectCreate("employee/office", Office.class);
	    digester.addSetNext("employee/office", "addOffice");
	    
	    
	    try {
	    	 Employee employee = (Employee) digester.parse(file);
	         System.out.println("First name : " + employee.getFirstName());
	         System.out.println("Last name : " + employee.getLastName());
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
}
