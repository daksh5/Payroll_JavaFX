import java.util.*;
import java.io.*;

public abstract class Employee implements Serializable {

	protected String login;					//username
	protected double payrate;				//to calculate employee's salary
	protected String name;					//name of the Employee
	protected Date hireDate;				//date when new employee was added into the system
	protected int employeeID;				//unique employee ID number, 5 digits, final
	protected byte[] password;			//password

	protected static int nextId=0;				//auto increment, holds temporary ID for an employee

	 //Constructor: creates a new employee 
	public Employee( String login, byte[] password ,double salaryOrHourRate,  String name) {
		this.login = login; 
		this.password=password;
		this.payrate =salaryOrHourRate;
		this.name = name;
		hireDate = new Date();
		employeeID = nextId;
		nextId++;
		
		}
	
	//Employee Constructor with five parameters
		public Employee (int employeeID, String login,byte[] password, double salaryOrHourRate, long date, String name) {
			this.employeeID=employeeID;
			this.login=login;
			this.password=password;
			this.payrate=salaryOrHourRate;
			this.hireDate=new Date(date);
			this.name=name;
			nextId=++employeeID;
		}
		//getting user name
		public String getName() {
			return name;
		}
		//getting login name
		public String getUserName()
	    {
	    	return login;
	    }
		
		//getting employee ID
	
	public int getEmployeeId() {
		return employeeID;
	}
	//getting date
	
	public long getDate(){ 
		return hireDate.getTime();
	}
	//getting salary
	public double getSalary(){ 
		return payrate;
	}
	//getting password
		public byte[] getPassword() {
			return password;
		}
	//setting salary	
	public void setSalary(double salaryOrHourRate){ 
		this.payrate=salaryOrHourRate;
	}
	
	public void setName(String name){  //setting for name
		this.name=name;
	}
	
	public abstract double getPay();   //Abstract method to pay employee

	public String toString(){
		return String.format("%05d\t%s\t%-10s\t%12.2f\t%d\t%s",employeeID, login, password.toString(),payrate,hireDate.getTime(), name);
	}
}

