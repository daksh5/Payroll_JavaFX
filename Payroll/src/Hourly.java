
import java.io.Serializable;

public class Hourly extends Employee implements Serializable {
	double hours;
	//Constructor with three parameters.generates new hourly employee
	public Hourly(String login,byte[] password, double salaryOrHourRate, String name) {
		super(login,password, salaryOrHourRate, name);
		}

	// Constructor with five parameters.reads hourly employee data from file
		public Hourly (int employeeID, String login,byte[] password, double salaryOrHourRate, long date, String name) {
			super(employeeID, login,password, salaryOrHourRate, date, name);
		}
	
	//function to pay hourly employee
	public double getPay() {
		
		return hours * super.payrate;
	}
	
	//setting hours

	public void setHours(double hours) {
		this.hours=hours;
	}
}
