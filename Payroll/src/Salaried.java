import java.io.Serializable;

public class Salaried extends Employee implements Serializable {
	//Constructor with three parameters.
	public Salaried(String login,byte[] password, double salaryOrHourRate, String name) {
			super(login,password, salaryOrHourRate, name);
	}
	

	// Constructor with five parameters.
	public Salaried(int employeeID,String login,byte[] password, double salaryOrHourRate,long date, String name) {
		super(employeeID, login,password, salaryOrHourRate, date, name);
		
	}
		// Function to pay salaried employee 
	public double getPay() {
		
		return super.payrate/24;
	}

}
