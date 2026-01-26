package edu.course.eventplanner;

import edu.course.eventplanner.model.Guest;
import edu.course.eventplanner.model.Task;
import edu.course.eventplanner.model.Venue;
import edu.course.eventplanner.service.GuestListManager;
import edu.course.eventplanner.service.SeatingPlanner;
import edu.course.eventplanner.service.TaskManager;
import edu.course.eventplanner.service.VenueSelector;
import edu.course.eventplanner.util.Generators;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class User {
    private static Scanner scanner = new Scanner(System.in);
    private static GuestListManager guestManager = new GuestListManager();
    private static VenueSelector venueSelector;
    private static SeatingPlanner seatingPlanner;
    private static TaskManager taskManager = new TaskManager();
    private static Venue selectedVenue;
    private static Map<Integer, List<Guest>> seatingChart;

    public static void main(String[] args) {
        System.out.println("=== Event Planning System ===\n");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Enter choice: ");
            System.out.println();

            switch (choice) {
                case 1:
                    loadSampleData();
                    break;
                case 2:
                    addGuest();
                    break;
                case 3:
                    removeGuest();
                    break;
                case 4:
                    selectVenue();
                    break;
                case 5:
                    generateSeatingChart();
                    break;
                case 6:
                    addPreparationTask();
                    break;
                case 7:
                    executeNextTask();
                    break;
                case 8:
                    undoLastTask();
                    break;
                case 9:
                    printEventSummary();
                    break;
                case 0:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("--- Main Menu ---");
        System.out.println("1. Load sample data");
        System.out.println("2. Add guest");
        System.out.println("3. Remove guest");
        System.out.println("4. Select venue");
        System.out.println("5. Generate seating chart");
        System.out.println("6. Add preparation task");
        System.out.println("7. Execute next task");
        System.out.println("8. Undo last task");
        System.out.println("9. Print event summary");
        System.out.println("0. Exit");
    }

    private static void loadSampleData() {
        System.out.print("How many guests to generate? ");
        int numGuests = getIntInput("");

        // Generate venues using helper
        List<Venue> venues = Generators.generateVenues();
        venueSelector = new VenueSelector(venues);
        System.out.println("Loaded " + venues.size() + " sample venues.");

        // Generate guests using helper and add via addGuest
        List<Guest> generatedGuests = Generators.GenerateGuests(numGuests);
        for (Guest g : generatedGuests) {
            guestManager.addGuest(g);
        }
        System.out.println("Added " + numGuests + " guests to the guest list.");

        // Add some sample tasks
        taskManager.addTask(new Task("Book venue"));
        taskManager.addTask(new Task("Send invitations"));
        taskManager.addTask(new Task("Order catering"));
        taskManager.addTask(new Task("Arrange decorations"));
        System.out.println("Added 4 sample preparation tasks.");
    }

    private static void addGuest() {
        System.out.print("Guest name: ");
        String name = scanner.nextLine();

        System.out.print("Group tag (family/friends/neighbors/coworkers): ");
        String groupTag = scanner.nextLine();

        Guest guest = new Guest(name, groupTag);
        guestManager.addGuest(guest);

        System.out.println("Guest '" + name + "' added successfully.");
    }

    private static void removeGuest() {
        System.out.print("Guest name to remove: ");
        String name = scanner.nextLine();

        boolean removed = guestManager.removeGuest(name);

        if (removed) {
            System.out.println("Guest '" + name + "' removed successfully.");
        } else {
            System.out.println("Guest '" + name + "' not found.");
        }
    }

    private static void selectVenue() {
        if (venueSelector == null) {
            System.out.println("Please load sample data first to get venues.");
            return;
        }

        System.out.print("Enter your budget: $");
        double budget = getDoubleInput("");

        int guestCount = guestManager.getGuestCount();
        System.out.println("Current guest count: " + guestCount);

        selectedVenue = venueSelector.selectVenue(budget, guestCount);

        if (selectedVenue != null) {
            System.out.println("\n--- Selected Venue ---");
            System.out.println("Name: " + selectedVenue.getName());
            System.out.println("Cost: $" + selectedVenue.getCost());
            System.out.println("Capacity: " + selectedVenue.getCapacity());
            System.out.println("Tables: " + selectedVenue.getTables());
            System.out.println("Seats per table: " + selectedVenue.getSeatsPerTable());
        } else {
            System.out.println("No venue found within budget and capacity constraints.");
        }
    }

    private static void generateSeatingChart() {
        if (selectedVenue == null) {
            System.out.println("Please select a venue first.");
            return;
        }

        if (guestManager.getGuestCount() == 0) {
            System.out.println("No guests to seat. Please add guests first.");
            return;
        }

        seatingPlanner = new SeatingPlanner(selectedVenue);
        seatingChart = seatingPlanner.generateSeating(guestManager.getAllGuests());

        System.out.println("--- Seating Chart ---");
        for (Map.Entry<Integer, List<Guest>> entry : seatingChart.entrySet()) {
            System.out.println("\nTable " + entry.getKey() + ":");
            for (Guest guest : entry.getValue()) {
                System.out.println("  - " + guest.getName() + " (" + guest.getGroupTag() + ")");
            }
        }

        System.out.println("\nTotal tables used: " + seatingChart.size());
    }

    private static void addPreparationTask() {
        System.out.print("Task description: ");
        String description = scanner.nextLine();

        Task task = new Task(description);
        taskManager.addTask(task);

        System.out.println("Task added. Remaining tasks: " + taskManager.remainingTaskCount());
    }

    private static void executeNextTask() {
        Task task = taskManager.executeNextTask();

        if (task != null) {
            System.out.println("Executed: " + task.getDescription());
            System.out.println("Remaining tasks: " + taskManager.remainingTaskCount());
        } else {
            System.out.println("No tasks to execute.");
        }
    }

    private static void undoLastTask() {
        Task task = taskManager.undoLastTask();

        if (task != null) {
            System.out.println("Undone: " + task.getDescription());
            System.out.println("Task returned to queue.");
        } else {
            System.out.println("No tasks to undo.");
        }
    }

    private static void printEventSummary() {
        System.out.println("=== Event Summary ===\n");

        System.out.println("Guests: " + guestManager.getGuestCount());

        if (selectedVenue != null) {
            System.out.println("\nVenue: " + selectedVenue.getName());
            System.out.println("Cost: $" + selectedVenue.getCost());
            System.out.println("Capacity: " + selectedVenue.getCapacity());
        } else {
            System.out.println("\nNo venue selected yet.");
        }

        if (seatingChart != null && !seatingChart.isEmpty()) {
            System.out.println("\nSeating: " + seatingChart.size() + " tables arranged");
        } else {
            System.out.println("\nNo seating chart generated yet.");
        }

        System.out.println("\nTasks:");
        System.out.println("  Remaining: " + taskManager.remainingTaskCount());
        System.out.println("  Completed: " + taskManager.completedTaskCount());
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}