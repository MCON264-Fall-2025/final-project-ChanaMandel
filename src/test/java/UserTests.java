
import edu.course.eventplanner.User;
import edu.course.eventplanner.model.Guest;
import edu.course.eventplanner.model.Task;
import edu.course.eventplanner.model.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User class methods.
 * These tests target the testable static methods to achieve 80% coverage.
 */
public class UserTests {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Reset User state before each test
        User.resetForTesting();

        // Capture System.out for verification
        System.setOut(new PrintStream(outputStream));
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    // TEST 1: loadSampleDataWithCount
    @Test
    public void testLoadSampleDataWithCount() {
        User.loadSampleDataWithCount(10);

        // Verify guests were added
        assertEquals(10, User.guestManager.getGuestCount(),
                "Should have 10 guests after loading");

        // Verify venues were loaded
        assertNotNull(User.venueSelector, "VenueSelector should be initialized");

        // Verify tasks were added
        assertEquals(4, User.taskManager.remainingTaskCount(),
                "Should have 4 tasks after loading");

        // Verify output
        String output = outputStream.toString();
        assertTrue(output.contains("Loaded"), "Should print venue loading message");
        assertTrue(output.contains("Added 10 guests"), "Should print guest loading message");
    }

    // TEST 2: loadSampleDataWithCount with different count
    @Test
    public void testLoadSampleDataWithDifferentCount() {
        User.loadSampleDataWithCount(25);

        assertEquals(25, User.guestManager.getGuestCount());
    }

    // TEST 3: addGuestWithDetails
    @Test
    public void testAddGuestWithDetails() {
        User.addGuestWithDetails("Alice", "family");

        assertEquals(1, User.guestManager.getGuestCount(),
                "Guest count should be 1");

        Guest found = User.guestManager.findGuest("Alice");
        assertNotNull(found, "Should be able to find added guest");
        assertEquals("Alice", found.getName());
        assertEquals("family", found.getGroupTag());

        String output = outputStream.toString();
        assertTrue(output.contains("Alice"), "Output should mention guest name");
        assertTrue(output.contains("added successfully"), "Output should confirm addition");
    }

    // TEST 4: addGuestWithDetails multiple guests
    @Test
    public void testAddMultipleGuestsWithDetails() {
        User.addGuestWithDetails("Alice", "family");
        User.addGuestWithDetails("Bob", "friends");
        User.addGuestWithDetails("Charlie", "coworkers");

        assertEquals(3, User.guestManager.getGuestCount());
        assertNotNull(User.guestManager.findGuest("Alice"));
        assertNotNull(User.guestManager.findGuest("Bob"));
        assertNotNull(User.guestManager.findGuest("Charlie"));
    }

    // TEST 5: removeGuestByName - successful removal
    @Test
    public void testRemoveGuestByName_Success() {
        User.addGuestWithDetails("Alice", "family");
        User.addGuestWithDetails("Bob", "friends");

        boolean removed = User.removeGuestByName("Alice");

        assertTrue(removed, "Removal should succeed");
        assertEquals(1, User.guestManager.getGuestCount(),
                "Guest count should decrease to 1");
        assertNull(User.guestManager.findGuest("Alice"),
                "Removed guest should not be found");

        String output = outputStream.toString();
        assertTrue(output.contains("removed successfully"),
                "Output should confirm removal");
    }

    // TEST 6: removeGuestByName - failed removal
    @Test
    public void testRemoveGuestByName_NotFound() {
        User.addGuestWithDetails("Alice", "family");

        boolean removed = User.removeGuestByName("NonExistent");

        assertFalse(removed, "Removal should fail for non-existent guest");
        assertEquals(1, User.guestManager.getGuestCount(),
                "Guest count should remain 1");

        String output = outputStream.toString();
        assertTrue(output.contains("not found"),
                "Output should indicate guest not found");
    }

    // TEST 7: selectVenueWithBudget - successful selection
    @Test
    public void testSelectVenueWithBudget_Success() {
        User.loadSampleDataWithCount(20);

        Venue selected = User.selectVenueWithBudget(3000, 20);

        assertNotNull(selected, "Should select a venue");
        assertTrue(selected.getCost() <= 3000,
                "Selected venue should be within budget");
        assertTrue(selected.getCapacity() >= 20,
                "Selected venue should have enough capacity");
        assertEquals(selected, User.selectedVenue,
                "Selected venue should be stored in static field");

        String output = outputStream.toString();
        assertTrue(output.contains("Selected Venue"),
                "Output should show venue details");
    }

    // TEST 8: selectVenueWithBudget - no venue found
    @Test
    public void testSelectVenueWithBudget_NoVenueFound() {
        User.loadSampleDataWithCount(10);

        Venue selected = User.selectVenueWithBudget(100, 200);

        assertNull(selected, "Should not find a venue with low budget and high guests");

        String output = outputStream.toString();
        assertTrue(output.contains("No venue found"),
                "Output should indicate no venue found");
    }

    // TEST 9: selectVenueWithBudget - without loading venues first
    @Test
    public void testSelectVenueWithBudget_NoVenuesLoaded() {
        Venue selected = User.selectVenueWithBudget(5000, 20);

        assertNull(selected, "Should return null when venues not loaded");

        String output = outputStream.toString();
        assertTrue(output.contains("load sample data first"),
                "Output should prompt to load data");
    }

    // TEST 10: generateSeatingChartForGuests - successful
    @Test
    public void testGenerateSeatingChartForGuests_Success() {
        User.loadSampleDataWithCount(10);
        User.selectVenueWithBudget(5000, 10);

        Map<Integer, List<Guest>> seating = User.generateSeatingChartForGuests();

        assertNotNull(seating, "Should generate seating chart");
        assertFalse(seating.isEmpty(), "Seating chart should not be empty");

        // Verify all guests are seated
        int totalSeated = seating.values().stream()
                .mapToInt(List::size)
                .sum();
        assertEquals(10, totalSeated, "All guests should be seated");

        String output = outputStream.toString();
        assertTrue(output.contains("Seating Chart"),
                "Output should display seating chart");
    }

    // TEST 11: generateSeatingChartForGuests - no venue selected
    @Test
    public void testGenerateSeatingChartForGuests_NoVenue() {
        User.loadSampleDataWithCount(10);

        Map<Integer, List<Guest>> seating = User.generateSeatingChartForGuests();

        assertNull(seating, "Should return null when no venue selected");

        String output = outputStream.toString();
        assertTrue(output.contains("select a venue first"),
                "Output should prompt to select venue");
    }

    // TEST 12: generateSeatingChartForGuests - no guests
    @Test
    public void testGenerateSeatingChartForGuests_NoGuests() {
        User.loadSampleDataWithCount(0);
        User.selectVenueWithBudget(5000, 1);

        Map<Integer, List<Guest>> seating = User.generateSeatingChartForGuests();

        assertNull(seating, "Should return null when no guests");

        String output = outputStream.toString();
        assertTrue(output.contains("No guests to seat"),
                "Output should indicate no guests");
    }

    // TEST 13: addPreparationTaskWithDescription
    @Test
    public void testAddPreparationTaskWithDescription() {
        User.addPreparationTaskWithDescription("Test Task");

        assertEquals(1, User.taskManager.remainingTaskCount(),
                "Should have 1 task");

        String output = outputStream.toString();
        assertTrue(output.contains("Task added"), "Output should confirm task added");
    }

    // TEST 14: addPreparationTaskWithDescription multiple tasks
    @Test
    public void testAddMultiplePreparationTasks() {
        User.addPreparationTaskWithDescription("Task 1");
        User.addPreparationTaskWithDescription("Task 2");
        User.addPreparationTaskWithDescription("Task 3");

        assertEquals(3, User.taskManager.remainingTaskCount());
    }

    // TEST 15: executeNextTask - successful execution
    @Test
    public void testExecuteNextTask_Success() {
        User.addPreparationTaskWithDescription("First Task");
        User.addPreparationTaskWithDescription("Second Task");

        Task executed = User.executeNextTask();

        assertNotNull(executed, "Should execute a task");
        assertEquals("First Task", executed.getDescription(),
                "Should execute first task (FIFO)");
        assertTrue(executed.isComplete(), "Executed task should be complete");
        assertEquals(1, User.taskManager.remainingTaskCount(),
                "Should have 1 remaining task");

        String output = outputStream.toString();
        assertTrue(output.contains("Executed"), "Output should show execution");
    }

    // TEST 16: executeNextTask - no tasks
    @Test
    public void testExecuteNextTask_NoTasks() {
        Task executed = User.executeNextTask();

        assertNull(executed, "Should return null when no tasks");

        String output = outputStream.toString();
        assertTrue(output.contains("No tasks to execute"),
                "Output should indicate no tasks");
    }

    // TEST 17: undoLastTask - successful undo
    @Test
    public void testUndoLastTask_Success() {
        User.addPreparationTaskWithDescription("Task to Undo");
        User.executeNextTask();

        assertEquals(0, User.taskManager.remainingTaskCount());
        assertEquals(1, User.taskManager.completedTaskCount());

        Task undone = User.undoLastTask();

        assertNotNull(undone, "Should undo a task");
        assertEquals("Task to Undo", undone.getDescription());
        assertFalse(undone.isComplete(), "Undone task should not be complete");
        assertEquals(1, User.taskManager.remainingTaskCount(),
                "Task should be back in queue");

        String output = outputStream.toString();
        assertTrue(output.contains("Undone"), "Output should show undo");
    }

    // TEST 18: undoLastTask - no tasks to undo
    @Test
    public void testUndoLastTask_NoTasks() {
        Task undone = User.undoLastTask();

        assertNull(undone, "Should return null when no tasks to undo");

        String output = outputStream.toString();
        assertTrue(output.contains("No tasks to undo"),
                "Output should indicate no tasks to undo");
    }

    // TEST 19: printEventSummary - with data
    @Test
    public void testPrintEventSummary_WithData() {
        User.loadSampleDataWithCount(15);
        User.selectVenueWithBudget(3000, 15);
        User.generateSeatingChartForGuests();
        User.executeNextTask();

        // Clear previous output
        outputStream.reset();

        User.printEventSummary();

        String output = outputStream.toString();
        assertTrue(output.contains("Event Summary"), "Should show summary header");
        assertTrue(output.contains("Guests: 15"), "Should show guest count");
        assertTrue(output.contains("Venue:"), "Should show venue info");
        assertTrue(output.contains("Seating:"), "Should show seating info");
        assertTrue(output.contains("Remaining: 3"), "Should show remaining tasks");
        assertTrue(output.contains("Completed: 1"), "Should show completed tasks");
    }

    // TEST 20: printEventSummary - with no data
    @Test
    public void testPrintEventSummary_NoData() {
        User.printEventSummary();

        String output = outputStream.toString();
        assertTrue(output.contains("Guests: 0"), "Should show 0 guests");
        assertTrue(output.contains("No venue selected"),
                "Should indicate no venue");
        assertTrue(output.contains("No seating chart"),
                "Should indicate no seating");
    }

    // TEST 21: printMenu
    @Test
    public void testPrintMenu() {
        User.printMenu();

        String output = outputStream.toString();
        assertTrue(output.contains("Main Menu"), "Should show menu header");
        assertTrue(output.contains("1. Load sample data"), "Should show option 1");
        assertTrue(output.contains("2. Add guest"), "Should show option 2");
        assertTrue(output.contains("9. Print event summary"), "Should show option 9");
        assertTrue(output.contains("0. Exit"), "Should show exit option");
    }

    // TEST 22: resetForTesting
    @Test
    public void testResetForTesting() {
        User.loadSampleDataWithCount(10);
        User.addGuestWithDetails("Extra", "family");

        User.resetForTesting();

        assertEquals(0, User.guestManager.getGuestCount(),
                "Guest count should be reset to 0");
        assertNull(User.venueSelector, "VenueSelector should be null");
        assertNull(User.selectedVenue, "Selected venue should be null");
        assertEquals(0, User.taskManager.remainingTaskCount(),
                "Task count should be reset to 0");
    }

    // TEST 23: Integration - full workflow
    @Test
    public void testFullWorkflow() {
        // Load data
        User.loadSampleDataWithCount(20);
        assertEquals(20, User.guestManager.getGuestCount());

        // Add a guest
        User.addGuestWithDetails("VIP Guest", "family");
        assertEquals(21, User.guestManager.getGuestCount());

        // Select venue
        Venue venue = User.selectVenueWithBudget(5000, 21);
        assertNotNull(venue);

        // Generate seating
        Map<Integer, List<Guest>> seating = User.generateSeatingChartForGuests();
        assertNotNull(seating);

        // Execute task
        Task task = User.executeNextTask();
        assertNotNull(task);

        // Undo task
        Task undone = User.undoLastTask();
        assertEquals(task.getDescription(), undone.getDescription());

        // Remove guest
        boolean removed = User.removeGuestByName("VIP Guest");
        assertTrue(removed);
        assertEquals(20, User.guestManager.getGuestCount());
    }
}
