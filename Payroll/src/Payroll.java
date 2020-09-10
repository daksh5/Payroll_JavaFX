/**

 * @author Daksh Parikh
 * @date December 15, 2018
 
 */

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;



public class Payroll extends Application {
	
	
	private String login, name;			//login-name and the real name of employee
	private double salaryOrHourRate;	//employee's salary (Salaried) or hourly rate (Hourly)
	private int currentUserID=-1;		//current user's ID, distinguish boss from regular employees
	private String currentUserLogin;	//current user's login
	private long date;					//date when employee was added
	private int employeeID;				//final and unique
	private int countRemoved;			//counts the number of employees removed from the database
	private char employeeCategory;		//salaried (s) or hourly (h)
	private byte[]  password;			//password, encrypted


	private ArrayList<Employee> list = new ArrayList<Employee>();	//An ArrayList holds current Employee objects
	private ArrayList<Employee> removed = new ArrayList<Employee>();//An ArrayList holds removed Employee objects
	
	//StringBuilder holds information of removed employees
		StringBuilder sbRemove = new StringBuilder("List of employees who are terminated: \n");

		Stage st;
		Scene sn;

		Font boldTNR = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 22);
		Font regSans  = Font.font("Monospace", FontWeight.NORMAL, FontPosture.REGULAR, 20);

		TextField     usernameField      = new TextField();
		TextField     salaryField        = new TextField();
		TextField     nameField          = new TextField();
		TextField     empCategoryField   = new TextField();
		TextField     IDField            = new TextField();
		TextArea      ta                 = new TextArea();

		PasswordField passwordField1 	 = new PasswordField();
		PasswordField passwordField2 	 = new PasswordField();

		Label 		  msg        	 = new Label("Boss:Enter your name: ");
		Label         uname          = new Label("Username");
		Label         password1     = new Label("Password");
		Label         repassword     = new Label("Re-type Password");
		Label         salaryS        = new Label("Salary/Hourly Rate");
		Label         actualname          = new Label("Actual Name");
		Label         empCat        = new Label("Category of Employeement");
		Label         IDLabel        	 = new Label("Employee ID");
		Label         empToPay       	 = new Label();

		Button        btLogin       	 = new Button("LogIn");
		Button        btNewEmp    		 = new Button("Add a new employee");
		Button        btListEmp    		 = new Button("List employees");
		Button        btPay         	 = new Button("Pay employees");
		Button        btChangeData 		 = new Button("Update employee data");
		Button        btTerminate   	 = new Button("Terminate an employee");
		Button        btMainmenu    	 = new Button("Main menu");
		Button        btSubmit      	 = new Button("Submit");
		RadioButton   rbSalaried         = new RadioButton("Salaried");
		RadioButton   rbHourly           = new RadioButton("Hourly");
		Button        btLogout      	 = new Button("Logout");
		ToggleGroup   groupCategory      = new ToggleGroup();


		public void start(Stage st) throws FileNotFoundException {
			this.st = st;
			buildGui();
			initLoad();
			st.setScene(sn);
			st.setTitle("Payroll");
			st.show();
		}

		// Initial function to read data from file 

		@SuppressWarnings("unchecked")
		public void initLoad () {
			try{
				//recover employee data from file
				FileInputStream fstream = new FileInputStream("employee.txt");
		        ObjectInputStream ois = new ObjectInputStream(fstream);
		        ArrayList<Employee> temp = (ArrayList<Employee>)
		        ois.readObject();
		        for (Employee e: temp) {
		        	//read employee information 
		        	employeeID = e.getEmployeeId();
		        	login = e.getUserName().trim();
					password = e.getPassword();

		        	salaryOrHourRate = e.getSalary();
		        	date = e.getDate();
		        	name = e.getName().trim();
		        	if (e instanceof Salaried) {
		        		list.add(new Salaried(employeeID, login, password, salaryOrHourRate, date, name));
		        	}
		        	else {
		        		//if employee is hourly paid, get hourly rate and create employee
		        		list.add(new Hourly(employeeID, login, password, salaryOrHourRate, date, name));
		        	}
		        }
		        ois.close();
		        buildLoginGui();
			}
			catch (FileNotFoundException fe){
				/*when no employee exists, create a new file employee.txt, create a boss account who can
				create new employee account and have full access to the database*/
				System.out.println("\nError! File employee.txt not found.");

				btSubmit.setOnAction(e->{			PrintWriter pw = null;
					byte[] tempPassword = null;
					try {
						pw = new PrintWriter (new File("employee.txt"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					pw.close();
					login = usernameField.getText();
					try {
						password = computeHash(passwordField1.getText());
						tempPassword = computeHash(passwordField2.getText());
					}
					catch (NoSuchAlgorithmException ne) {
						ne.printStackTrace();
					}
					if (password != null && !Arrays.equals(password, tempPassword))
						System.out.print("\nPasswords does't match! ");
					salaryOrHourRate = Double.parseDouble(salaryField.getText());
					name = nameField.getText();
					list.add(new Salaried(login, password, salaryOrHourRate, name));
					buildLoginGui();
				});
			}
			catch (ClassNotFoundException ce) {
				ce.printStackTrace();
			}
			catch (IOException ie) {
				ie.printStackTrace();
			}
		}

		// Builds the initial GUI when there is no employee 

		public void buildGui() {
			GridPane gp = new GridPane();
			VBox vb = new VBox(10);
			StackPane sp = new StackPane();

			msg       .setTextFill(Color.TOMATO);
			ta      	  .setFont(regSans);
			btSubmit	  .setFont(boldTNR);
			msg 	  .setFont(boldTNR);
			usernameField .setFont(regSans);
			salaryField   .setFont(regSans);
			passwordField1.setFont(regSans);
			passwordField2.setFont(regSans);
			nameField     .setFont(regSans);
			uname     .setFont(boldTNR);
			password1.setFont(boldTNR);
			repassword.setFont(boldTNR);
			salaryS   .setFont(boldTNR);
			actualname     .setFont(boldTNR);

			btSubmit      .setPrefWidth(150);
			btMainmenu    .setPrefWidth(300);
			usernameField .setPrefWidth(150);
			passwordField1.setPrefWidth(300);
			passwordField2.setPrefWidth(300);
			salaryField   .setPrefWidth(300);
			nameField     .setPrefWidth(300);

			gp.add(uname, 0, 0);
			gp.add(usernameField, 1, 0);
			gp.add(password1, 0, 1);
			gp.add(passwordField1, 1, 1);
			gp.add(repassword, 0, 2);
			gp.add(passwordField2, 1, 2);
			gp.add(salaryS, 0, 3);
			gp.add(salaryField, 1, 3);
			gp.add(actualname, 0, 4);
			gp.add(nameField, 1, 4);
			gp.add(btSubmit, 1, 5);
			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(5,5,5,5));
			gp.setAlignment(Pos.CENTER);

			vb.getChildren().addAll(msg, gp, ta);
			vb.setAlignment(Pos.CENTER);
			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 150, 150);
		}

		// Builds the login menu GUI

		public void buildLoginGui() {
			GridPane gp = new GridPane();
			VBox vb = new VBox(10);
			StackPane sp = new StackPane();
			msg.setText("Payroll System!");

			usernameField .setPrefWidth(300);
			passwordField1.setPrefWidth(300);

			btSubmit      .setFont(boldTNR);
			btLogin       .setFont(boldTNR);
			msg       .setFont(boldTNR);
			usernameField .setFont(regSans);
			passwordField1.setFont(regSans);
			uname     .setFont(boldTNR);
			password1.setFont(boldTNR);

			usernameField .setText("");
			passwordField1.setText("");
			passwordField2.setText("");

			gp.add(uname, 0, 0);
			gp.add(usernameField, 1, 0);
			gp.add(password1, 0, 1);
			gp.add(passwordField1, 1, 1);
			gp.add(btLogin, 1, 2);
			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);
			vb.getChildren().addAll(msg, gp);
			vb.setAlignment(Pos.CENTER);

			btLogin.setOnAction(e -> {doLogin(); usernameField.setText(""); passwordField1.setText("");});

			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 800, 600);
			st.setScene(sn);
		}

		 // Builds GUI for the boss employee main menu 

		public void buildBossGui() {
			VBox vb = new VBox(10);
			vb.setPadding(new Insets(20,20,20,20));
			StackPane sp = new StackPane();
			msg.setText("Welcome to the Boss login. Please make a Choice.");
			ta.setText("");

			btNewEmp    .setPrefWidth(300);
			btListEmp   .setPrefWidth(300);
			btChangeData.setPrefWidth(300);
			btTerminate .setPrefWidth(300);
			btPay       .setPrefWidth(300);
			btLogout    .setPrefWidth(300);

			btNewEmp    .setFont(boldTNR);
			btListEmp   .setFont(boldTNR);
			btChangeData.setFont(boldTNR);
			btTerminate .setFont(boldTNR);
			btPay       .setFont(boldTNR);
			btLogout    .setFont(boldTNR);

			vb.getChildren().addAll(msg, btNewEmp, btListEmp, btChangeData, btTerminate, btPay, btLogout, ta);
			vb.setAlignment(Pos.CENTER);
			btNewEmp    .setOnAction( e -> {
									usernameField.setText("");
									passwordField1.setText("");
									salaryField.setText("");
									nameField.setText("");
									ta.setText("");
									buildAddNewEmployeeGui(); } );
			btListEmp   .setOnAction( e -> listEmployee() );
			btChangeData.setOnAction( e -> {
									ta.setText("");
									salaryField.setText("");
									nameField.setText("");
									IDField.setText("");
									buildChangeDataGui();} );
			btTerminate .setOnAction( e -> {
				ta.setText("");
				salaryField.setText("");
				buildTerminateEmployeeGui();
				ta.setText("");
				} );
			btPay       .setOnAction( e -> { ta.setText(""); salaryField.setText(""); buildPayEmployeeGui(); } );
			btLogout    .setOnAction( e -> { ta.setText("");  logout(); countRemoved = 0; buildLoginGui(); } );

			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 800, 600);
			st.setScene(sn);
		}

		// Builds GUI of the non-boss employee main menu

		public void buildEmployeeGui(){
			StackPane sp = new StackPane();
			VBox vb = new VBox(10);
			msg.setText("Kindly choose an option through the buttons.");

			btListEmp  .setFont(boldTNR);
			btTerminate.setFont(boldTNR);
			btLogout   .setFont(boldTNR);
			btListEmp  .setPrefWidth(300);
			btTerminate.setPrefWidth(300);
			btLogout   .setPrefWidth(300);

			btListEmp  .setOnAction(e -> listEmployee());
			btTerminate.setOnAction(e -> {ta.setText("");buildTerminateEmployeeGui();});
			btLogout   .setOnAction(e -> {ta.setText(""); buildLoginGui();});

			vb.getChildren().addAll(msg, btListEmp, btTerminate, btLogout, ta);
			vb.setAlignment(Pos.CENTER);
			vb.setPadding(new Insets(10));
			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 800, 600);
			st.setScene(sn);
		}
		// This screen is only visible to the boss.
		// All fields are required. No field can be left empty.

		public void buildAddNewEmployeeGui() {
			GridPane gp = new GridPane();
			StackPane sp = new StackPane();
			VBox vb = new VBox(10);
			ta.setText("Please enter new employee's details:");
			msg.setText("Enter the details of the new employee");
			Label message1 = new Label("Please Click OK to go back to homepage!");
			message1.setTextFill(Color.ORANGE);
			message1.setFont(boldTNR);

			btMainmenu.setText("OK");
			btMainmenu.setFont(boldTNR);
			btMainmenu.setPrefWidth(350);
			btSubmit  .setPrefWidth(350);

			rbSalaried.setToggleGroup(groupCategory);
			rbSalaried.setUserData("S");
			rbSalaried.setSelected(true);
			rbHourly  .setToggleGroup(groupCategory);
			rbHourly  .setUserData("H");

			rbSalaried.setFont(boldTNR);
			rbHourly  .setFont(boldTNR);

			gp.add(rbSalaried, 0, 0);
			gp.add(rbHourly, 1, 0);
			gp.add(uname, 0, 2);
			gp.add(usernameField, 1, 2);
			gp.add(password1, 0, 3);
			gp.add(passwordField1, 1, 3);
			gp.add(repassword, 0, 4);
			gp.add(passwordField2, 1, 4);
			gp.add(salaryS, 0, 5);
			gp.add(salaryField, 1, 5);
			gp.add(actualname, 0, 6);
			gp.add(nameField, 1, 6);
			gp.add(btSubmit, 1, 7);
			gp.add(btMainmenu, 1, 8);
			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);

			vb.getChildren().addAll(msg, message1, gp, ta);
			vb.setAlignment(Pos.CENTER);
			btSubmit.setOnAction(e -> {
				newEmployee();
				usernameField.setText("");
				passwordField1.setText("");
				passwordField2.setText("");
				salaryField.setText("");
				nameField.setText("");
				});
			if(currentUserID == 0) {
				btMainmenu.setOnAction(e -> {ta.setText(""); buildBossGui();});
			}
			else {
				btMainmenu.setOnAction(e -> {ta.setText(""); buildEmployeeGui();});
			}
			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 800, 600);
			st.setScene(sn);

		}

		//  Builds the change employee data GUI.
		
		//  The boss can change the name or salary or both.

		public void buildChangeDataGui() {
			GridPane gp = new GridPane();
			StackPane sp = new StackPane();
			VBox vb = new VBox(10);
			Label message1 = new Label("Click OK to GO to main menu");

			msg   .setText("Enter the ID, new name and salary of the employee");
			message1  .setTextFill(Color.ORANGE);
			btMainmenu.setText("OK");
			message1  .setFont(boldTNR);
			IDField   .setPrefWidth(300);
			IDField   .setFont(regSans);
			IDLabel   .setFont(boldTNR);
			btMainmenu.setFont(boldTNR);
			btMainmenu.setPrefWidth(300);
			btSubmit  .setPrefWidth(300);

			gp.add(IDLabel, 0, 0);
			gp.add(IDField, 1, 0);
			gp.add(salaryS, 0, 1);
			gp.add(salaryField, 1, 1);
			gp.add(actualname, 0, 2);
			gp.add(nameField, 1, 2);
			gp.add(btSubmit, 1, 3);
			gp.add(btMainmenu, 1, 4);

			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);

			vb.getChildren().addAll(msg, message1, gp, ta);
			vb.setAlignment(Pos.CENTER);
			btSubmit.setOnAction(e -> changeEmployeeData());
			btMainmenu.setOnAction(e -> {ta.setText(""); buildBossGui();});

			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 800, 600);
			st.setScene(sn);
		}

		 // Builds the pay employee GUI.
		 // The boss only pay employee once every time he logs in.
		
		public void buildPayEmployeeGui() {
			GridPane gp = new GridPane();
			StackPane sp = new StackPane();
			VBox vb = new VBox(10);
			Label hoursLabel = new Label("Hours");
			Label message1 = new Label("Click OK to return to main menu");
			message1  .setTextFill(Color.ORANGE);
			message1  .setFont(boldTNR);

			msg   .setText("Paying employees");
			btMainmenu .setText("OK");
			btMainmenu.setFont(boldTNR);
			empToPay  .setFont(boldTNR);
			hoursLabel.setFont(boldTNR);

			gp.add(hoursLabel, 0, 0);
			gp.add(salaryField, 1, 0);
			gp.add(btSubmit, 1, 1);
			gp.add(btMainmenu, 2, 1);
			gp.setVgap(20);
			gp.setHgap(20);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);
			empToPay.setText ("Boss is salaried. Press Submit ");

			vb.getChildren().addAll(msg, message1, empToPay, gp, ta);
			vb.setAlignment(Pos.CENTER);

			btSubmit.setOnAction( e-> {
				countRemoved++;
				if (countRemoved<list.size()) {
					if (list.get(countRemoved) instanceof Hourly) {
						empToPay.setText(list.get(countRemoved).getName()+ " is hourly paid. Enter hours and press Submit ");
					}
					else {
						empToPay.setText(list.get(countRemoved).getName()+ " is salaried. Press Submit");
					}
					pay();
					printPay();
				}
				else if (countRemoved==list.size()) {
					pay();
					printPay();
					empToPay.setText("All of the employees have been paid.");
				}
				else {
					empToPay.setText("All of the employees have been paid.");
				}
			});

			if(currentUserID == 0) {
				btMainmenu.setOnAction(e -> {ta.setText(""); buildBossGui();});
			}
			else {
				btMainmenu.setOnAction(e -> {ta.setText(""); buildEmployeeGui();});
			}
			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 800, 600);
			st.setScene(sn);
		}

		 // pay employee
		public void pay(){
			int length = list.size();
			double hours;
			Employee emp;
			if (countRemoved <= length){
				emp = list.get(countRemoved-1);
				if (emp instanceof Hourly) {
				
					if (!salaryField.getText().equals("")) {
						hours = Double.parseDouble(salaryField.getText());
						((Hourly) emp).setHours(hours);
					}
				}
			}
			else{
				empToPay.setText("All of the employees have been paid.");
			}
			salaryField.setText("");
		}

		// Print pay employee information

		public void printPay() {
			PrintWriter pay = null;
			StringBuilder employeePay = new StringBuilder();
			employeePay.append("\n\n------------------------------------------\n\t   Pay to employees\n");
			employeePay.append("   "+new Date().toString()+"\n------------------------------------------\n");
			employeePay.append(String.format("%5s%2s%15s%2s%8s\n\n", "ID  ", " ", "Name      ", " ", "Pay($)  "));
			try {
				pay = new PrintWriter(new File("payroll.txt"));
				for (Employee em: list) {
					pay.printf("%05d   %-15s%12.2f\n", em.getEmployeeId(), em.getName(), em.getPay());
					pay.println();
					employeePay.append(String.format("%05d%4s%-15s%4s%8.2f\n\n",
							em.getEmployeeId()," ", em.getName()," ", em.getPay()));
				}
			}
			catch (FileNotFoundException fe) {
				fe.printStackTrace();
			}
			finally{
				pay.close();
			}
			ta.setText(employeePay.toString());
		}

		// Builds the terminate employee GUI.
		public void buildTerminateEmployeeGui() {
			GridPane gp = new GridPane();
			StackPane sp = new StackPane();
			VBox vb = new VBox(10);
			sbRemove.append(String.format("\n%5s%2s%15s\n", "ID  "," ", "Name       "));

			Label message1 = new Label("Click OK to return to main menu");
			message1  .setTextFill(Color.BLUE);
			message1  .setFont(boldTNR);

			IDField   .setPrefWidth(300);
			IDField   .setFont(regSans);
			IDLabel   .setFont(boldTNR);
			btMainmenu.setFont(boldTNR);

			gp.add(IDLabel, 0, 0);
			gp.add(IDField, 1, 0);
			gp.add(btSubmit, 1, 1);
			gp.add(btMainmenu, 2, 1);

			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);

			if(currentUserID == 0) {
				msg.setText("Enter the employee's ID to terminate");
				btMainmenu.setOnAction(e -> {ta.setText(""); buildBossGui();});
				btSubmit.setOnAction(e-> terminate());
				vb.getChildren().addAll(msg, message1, gp, ta);
			}
			else {
				msg.setText("Do you want to quit? Press Submit to quit!");
				gp.getChildren().remove(IDLabel);
				gp.getChildren().remove(IDField);
				btMainmenu.setOnAction(e -> {ta.setText(""); buildEmployeeGui();});
				btSubmit.setOnAction( e-> {terminate(); logout(); buildLoginGui();});
				vb.getChildren().addAll(msg, message1, gp);
			}

			vb.setAlignment(Pos.CENTER);
			sp.getChildren().addAll(vb);
			sn = new Scene(sp, 800, 600);
			st.setScene(sn);
		}

		 // This function validate new login to assure it's uniqueness.
		 // It also set currentUserLogin and currentUserID.

		public void doLogin() {
			byte[] tempPassword = null;

			currentUserLogin = usernameField.getText();
			Employee currentEmp = isEmployee(list, currentUserLogin);
			try {
				tempPassword = computeHash((passwordField1.getText()));

				if (currentEmp != null &&
						Arrays.equals(currentEmp.getPassword(), tempPassword))
				 {
					msg.setText("Welcome! You logged in as "+currentUserLogin+".");
					setCurrentUserID(currentUserLogin);
					if (currentUserID == 0) buildBossGui();
					else buildEmployeeGui();
				}
				else {
					msg.setText("Either the username or password is invalid.");
				}

			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		 // This function checks whether the new login already exists.

		public Employee isEmployee(ArrayList<Employee> al, String empLogin){
			for (Employee em: al){
				if (empLogin.equals(em.getUserName())) return em;
			}
			return null;
		}

		// This function finds and returns an employee by searching login.

		public Employee findEmployee (ArrayList<Employee> al, int ID){
			for (Employee e: al) {
				if (ID == e.getEmployeeId())
					return e;
			}
			return null;
		}

		// Function to set currentUserID .

		public void setCurrentUserID(String login) {
			for (Employee em: list){
				if (login.equals(em.getUserName())) currentUserID = em.getEmployeeId();
			}
		}
		 //  Encrypt a string using SHA-256.
		 //  x  a String to be encrypted
		 //  an encrypted String
		 //  NoSuchAlgorithmException if the local system does not support SHA-1.

		 private static byte[] computeHash( String x ) throws NoSuchAlgorithmException {
		      MessageDigest d = MessageDigest.getInstance("SHA-256");
		      d.update(x.getBytes());
		      return  d.digest();
		 }

		// This function creates a new employee by calling Employee constructor.

		 public void newEmployee()  {
			 Employee newEmp;
			 employeeCategory = groupCategory.getSelectedToggle().getUserData().toString().charAt(0);
			 if (usernameField.getText().equals("") || usernameField.getText().equals("") ||
					 passwordField1.getText().equals("") || passwordField2.getText().equals("") ||
					 salaryField.getText().equals("") || nameField.getText().equals("")) {
				 ta.setText("ERROR! No field can be empty!");
			 }
			 else {
				 login = usernameField.getText();
				 if (isEmployee(list, login)!=null){
					 ta.setText("\nThe login already exists. Enter the employee's login: ");
				 }
				 else {
					 try {
						 password = computeHash(passwordField1.getText());
					 } catch (NoSuchAlgorithmException e) {

					 	e.printStackTrace();
					 }
					 salaryOrHourRate = Double.parseDouble(salaryField.getText());
					 name = nameField.getText();
					 switch(employeeCategory){
					 	case 'S':
					 		newEmp = new Salaried(login, password, salaryOrHourRate, name);
					 		break;
					 	case 'H':
					 		newEmp = new Hourly(login, password, salaryOrHourRate, name);
					 		break;
					 	default:
					 		newEmp = null;
					 }
					 if (newEmp != null) {
						 list.add(newEmp);
						 ta.setText("The following employee has been added:\n"+newEmp.toString());
					 }
			 	 }
			 }
		}

		 // Print employee information.

		 public void listEmployee() {
			StringBuilder employeeList = new StringBuilder();
			employeeList.append(String.format("%5s%2s%10s%2s%10s%2s%10s%2s%15s%2s%15s",
					"ID  ", " ", "username ", " ", "Password ", " ", "Salary  ", " ", "Date     ", " ", "Name      \n\n"));

			if (currentUserID == 0) {
				//when logged in as boss, list all employee's information
				for (Employee em:list) {
					employeeList.append(em.toString()+"\n");
				}
			}
			else {
				//when logged in as employee, list own information
				employeeList.append(findEmployee(list, currentUserID).toString()+"\n");
			}
			ta.setText(employeeList.toString());
		}

		// Function to change employee's name or salary or both.

		public void changeEmployeeData() {
			Employee em = null;	    //Employee object
			int employeeToChange;	//employee's login
			double newSalary = 0;	//new salary or hourly rate
			String newName = null, oldName = null, info = "";
			if (!IDField.getText().equals("")) {
				employeeToChange = Integer.parseInt(IDField.getText());
				em = findEmployee(list, employeeToChange);
				if (em != null ) {
					oldName = em.getName();
					newName = oldName;
					if (!salaryField.getText().equals("")) {
						newSalary=Double.parseDouble(salaryField.getText());
						em.setSalary(newSalary);
						info += oldName + "'s salary has been changed to " + newSalary + ".\n";
					}
					if (!nameField.getText().equals("")) {
						newName=nameField.getText();
						em.setName(newName);
						info += oldName + "'s name hae been changed to " + newName+"\n";
					}
					ta.setText(info);
				}
			}
			else ta.setText("The employee ID is invalid.");
			salaryField.setText("");
			nameField.setText("");
			IDField.setText("");
		}

		// Function for the boss to remove an employee, or an employee to quit the job.
		// To return to main menu, enter 0.

		public void terminate(){
			int employeeIDToRemove;
			Employee empToRemove;

			//when logged in as the boss, boss can terminate an employee
			if (currentUserID == 0) {
				if (!IDField.getText().equals("")) {
					employeeIDToRemove = Integer.parseInt(IDField.getText());
					if (findEmployee(list, employeeIDToRemove) == null) {
						msg.setText("ERROR! The ID number is invalid.");
					}
				//remove the selected employee from list and add it to empToremove list
					else {
						empToRemove = findEmployee(list, employeeIDToRemove);
						removed.add(empToRemove);
						list.remove(empToRemove);
						System.out.printf("\nEmployee %s has been removed from the system.\n", empToRemove.getName());
						sbRemove.append(String.format("\n%05d%2s%-15s\n",
								empToRemove.getEmployeeId()," ", empToRemove.getName()));
					}
				}
			}
			//when logged in as an employee, he can quit
			else {
				empToRemove = findEmployee(list, currentUserID);
				removed.add(empToRemove);
				list.remove(empToRemove);
				System.out.printf("\nEmployee %s has been removed from the system.\n", empToRemove.getName());
			}
			ta.setText(sbRemove.toString());
			IDField.setText("");
		}


		// logout

		public void logout() {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream("employee.txt");
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        ObjectOutputStream oos  = null;
	        try{
	        	oos  = new ObjectOutputStream(fos);
	        	oos.writeObject(list);
				System.out.println("\nFile employee.txt has been updated correctly.");
	        }
	        catch (IOException e) {
				e.printStackTrace();
			}
	        finally {
	        	try {
					fos.flush();
					fos.close();
				}
	        	catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}

		// main function used in IDE.

		public static void main (String[] args) {
			Application.launch(args);
		}
	}

	
