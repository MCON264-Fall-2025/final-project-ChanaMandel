package edu.course.eventplanner;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        System.out.println("Event Planner");

        double budget = 0;
        int numGuests = 0;
        Scanner user = new Scanner(System.in);
        System.out.println("Enter your budget: ");
        budget = user.nextDouble();
        while (budget <= 0 ){
            System.out.println("Please enter a valid budget");
            budget = user.nextInt();
        }

        System.out.println("Enter the munber of Guests: ");
        numGuests = user.nextInt();
        while(numGuests < 0 ){
            System.out.println("Please enter a valid munber of Guests");
        }
    }
}
