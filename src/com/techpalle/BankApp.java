package com.techpalle;

import java.sql.*;
import java.util.Scanner;

public class BankApp {
	public static final String url = "jdbc:mysql://localhost:3306/tap";
	public static final String un = "root";
	public static final String pwd = "ajaybr";
	
	public static Connection con = null;
	public static PreparedStatement ps1 = null;
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			con = DriverManager.getConnection(url,un,pwd);
			
			Scanner sc = new Scanner(System.in);
			
//			LOGIN MODULE
			System.out.println("<----Welcome to ABI Bank---->");
			System.out.println("Enter Account number: ");
			int acc_num = sc.nextInt();
			System.out.println("Enter the Pin: ");
			int pin = sc.nextInt();
			
			String qry = "select * from account where acc_num = ? and pin = ?";
			ps1 = con.prepareStatement(qry);
			ps1.setInt(1, acc_num);
			ps1.setInt(2, pin);
			
			ResultSet res1 = ps1.executeQuery();
			
			res1.next();
			String name = res1.getString(2);
			int bal = res1.getInt(4);
			System.out.println("Welcome "+name);
			System.out.println("Available balance: "+bal);
			
//			TRANSFER MODULE:
			System.out.println("<----Transfer details---->");
			System.out.println("Enter the beneficiary account number ");
			int bacc_num = sc.nextInt();
			System.out.println("Enter the transfer ammount ");
			int t_amount = sc.nextInt();
			
			con.setAutoCommit(false);
			
			PreparedStatement ps2 = con.prepareStatement("update account set balance  = balance - ? where acc_num = ?");
			ps2.setInt(1, t_amount);
			ps2.setInt(2, acc_num);
			
			ps2.executeUpdate();
			
			System.out.println("<----Incoming credit request---->");
			System.out.println(name +" account number "+ acc_num +" wants to trnsfer "+ t_amount);
			System.out.println("Press to Y to recieve");
			System.out.println("Press to N to reject");
			String choice = sc.next();
			
			if (choice.equals("Y")) {
				PreparedStatement ps3 = con.prepareStatement("update account set balance  = balance + ? where acc_num = ?");
				ps3.setInt(1, t_amount);
				ps3.setInt(2, bacc_num);
				
				ps3.executeUpdate();
				
				PreparedStatement ps4 = con.prepareStatement("select * from account where acc_num = ?"); 
				ps4.setInt(1, bacc_num);
				ResultSet res2 = ps4.executeQuery(); 
				res2.next();
				
				System.out.println("Your updated balance is: "+res2.getInt(4));
			} else {
				PreparedStatement ps5 = con.prepareStatement("select * from account where acc_num = ?"); 
				ps5.setInt(1, bacc_num);
				ResultSet res2 = ps5.executeQuery(); 
				res2.next();
				
				System.out.println("Existing balance is: "+res2.getInt(4));
			}
			
			con.commit();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
