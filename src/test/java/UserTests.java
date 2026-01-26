
import edu.course.eventplanner.model.Guest;
import edu.course.eventplanner.model.Task;
import edu.course.eventplanner.model.Venue;
import edu.course.eventplanner.service.GuestListManager;
import edu.course.eventplanner.service.SeatingPlanner;
import edu.course.eventplanner.service.TaskManager;
import edu.course.eventplanner.service.VenueSelector;
import edu.course.eventplanner.util.Generators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that simulate the full workflow of the User class.
 * These tests verify that all components work together correctly.
 */
public class UserTests {

    private GuestListManager guestManager;
    private VenueSelector venueSelector;
    private SeatingPlanner seatingPlanner;
    private TaskManager taskManager;
    private Venue selectedVenue;

    @BeforeEach
    public void setUp() {
        guestManager = new GuestListManager();
        taskManager = new TaskManager();

        // Initialize venues
        List<Venue> venues = Generators.generateVenues();
        venueSelector = new VenueSelector(venues);
    }

    @Test
    public void testCompleteEventPlanningWorkflow() {
        // Step 1: Load sample data (simulating menu option 1)
        int numGuests = 30;
        List<Guest> generatedGuests = Generators.GenerateGuests(numGuests);
        for (Guest g : generatedGuests) {
            guestManager.addGuest(g);
        }

        assertEquals(30, guestManager.getGuestCount(),
                "Should have 30 guests after loading sample data");

        // Step 2: Select venue (simulating menu option 4)
        double budget = 3000;
        int guestCount = guestManager.getGuestCount();
        selectedVenue = venueSelector.selectVenue(budget, guestCount);

        assertNotNull(selectedVenue, "Should find a venue within budget");
        assertTrue(selectedVenue.getCost() <= budget, "Venue cost should be within budget");
        assertTrue(selectedVenue.getCapacity() >= guestCount,
                "Venue capacity should accommodate all guests");

        // Step 3: Generate seating chart (simulating menu option 5)
        seatingPlanner = new SeatingPlanner(selectedVenue);
        Map<Integer, List<Guest>> seatingChart =
                seatingPlanner.generateSeating(guestManager.getAllGuests());

        assertNotNull(seatingChart, "Seating chart should be generated");
        assertFalse(seatingChart.isEmpty(), "Seating chart should have tables");

        // Verify all guests are seated
        int totalSeated = seatingChart.values().stream()
                .mapToInt(List::size)
                .sum();
        assertEquals(30, totalSeated, "All guests should be seated");

        // Step 4: Add preparation tasks (simulating menu option 6)
        taskManager.addTask(new Task("Book venue"));
        taskManager.addTask(new Task("Send invitations"));
        taskManager.addTask(new Task("Order catering"));

        assertEquals(3, taskManager.remainingTaskCount(),
                "Should have 3 tasks in queue");

        // Step 5: Execute tasks (simulating menu option 7)
        Task task1 = taskManager.executeNextTask();
        assertNotNull(task1, "Should execute first task");
        assertEquals("Book venue", task1.getDescription());
        assertTrue(task1.isComplete(), "Executed task should be marked complete");

        assertEquals(2, taskManager.remainingTaskCount(),
                "Should have 2 remaining tasks");
        assertEquals(1, taskManager.completedTaskCount(),
                "Should have 1 completed task");

        // Step 6: Undo last task (simulating menu option 8)
        Task undone = taskManager.undoLastTask();
        assertNotNull(undone, "Should undo the last task");
        assertEquals("Book venue", undone.getDescription());
        assertFalse(undone.isComplete(), "Undone task should not be complete");

        assertEquals(3, taskManager.remainingTaskCount(),
                "Should have 3 tasks back in queue");
        assertEquals(0, taskManager.completedTaskCount(),
                "Should have 0 completed tasks");
    }

    @Test
    public void testAddGuestWorkflow() {
        // Simulating menu option 2
        Guest newGuest = new Guest("Alice", "family");
        guestManager.addGuest(newGuest);

        assertEquals(1, guestManager.getGuestCount(),
                "Guest count should increase to 1");

        Guest found = guestManager.findGuest("Alice");
        assertNotNull(found, "Should be able to find the added guest");
        assertEquals("Alice", found.getName());
        assertEquals("family", found.getGroupTag());
    }

    @Test
    public void testRemoveGuestWorkflow() {
        // Setup: Add some guests first
        guestManager.addGuest(new Guest("Alice", "family"));
        guestManager.addGuest(new Guest("Bob", "friends"));
        guestManager.addGuest(new Guest("Charlie", "coworkers"));

        assertEquals(3, guestManager.getGuestCount());

        // Simulating menu option 3 - successful removal
        boolean removed = guestManager.removeGuest("Bob");

        assertTrue(removed, "Removal should succeed for existing guest");
        assertEquals(2, guestManager.getGuestCount(),
                "Guest count should decrease to 2");
        assertNull(guestManager.findGuest("Bob"),
                "Removed guest should not be found");

        // Simulating menu option 3 - failed removal
        boolean removedNonExistent = guestManager.removeGuest("NonExistent");

        assertFalse(removedNonExistent,
                "Removal should fail for non-existent guest");
        assertEquals(2, guestManager.getGuestCount(),
                "Guest count should remain 2");
    }

    @Test
    public void testSeatingChartGeneration() {
        // Setup: Add guests with different group tags
        guestManager.addGuest(new Guest("Alice", "family"));
        guestManager.addGuest(new Guest("Bob", "family"));
        guestManager.addGuest(new Guest("Charlie", "friends"));
        guestManager.addGuest(new Guest("Diana", "friends"));
        guestManager.addGuest(new Guest("Eve", "coworkers"));

        // Select a venue
        selectedVenue = venueSelector.selectVenue(5000, 5);
        assertNotNull(selectedVenue, "Should select a venue");

        // Generate seating
        seatingPlanner = new SeatingPlanner(selectedVenue);
        Map<Integer, List<Guest>> seatingChart =
                seatingPlanner.generateSeating(guestManager.getAllGuests());

        assertNotNull(seatingChart, "Seating chart should be generated");

        // Verify all guests are seated
        int totalSeated = seatingChart.values().stream()
                .mapToInt(List::size)
                .sum();
        assertEquals(5, totalSeated, "All 5 guests should be seated");

        // Verify no table exceeds capacity
        for (List<Guest> table : seatingChart.values()) {
            assertTrue(table.size() <= selectedVenue.getSeatsPerTable(),
                    "No table should exceed seats per table limit");
        }
    }

    @Test
    public void testTaskExecutionFIFOOrder() {
        // Add tasks in specific order
        taskManager.addTask(new Task("First"));
        taskManager.addTask(new Task("Second"));
        taskManager.addTask(new Task("Third"));

        // Execute and verify FIFO order
        Task t1 = taskManager.executeNextTask();
        assertEquals("First", t1.getDescription(), "First task should execute first");

        Task t2 = taskManager.executeNextTask();
        assertEquals("Second", t2.getDescription(), "Second task should execute second");

        Task t3 = taskManager.executeNextTask();
        assertEquals("Third", t3.getDescription(), "Third task should execute third");

        // Verify queue is empty
        assertEquals(0, taskManager.remainingTaskCount());
        Task noTask = taskManager.executeNextTask();
        assertNull(noTask, "Should return null when no tasks remain");
    }

    @Test
    public void testTaskUndoLIFOOrder() {
        // Add and execute multiple tasks
        taskManager.addTask(new Task("Task A"));
        taskManager.addTask(new Task("Task B"));
        taskManager.addTask(new Task("Task C"));

        taskManager.executeNextTask(); // Execute A
        taskManager.executeNextTask(); // Execute B
        taskManager.executeNextTask(); // Execute C

        assertEquals(3, taskManager.completedTaskCount());

        // Undo in LIFO order (Last In, First Out)
        Task undone1 = taskManager.undoLastTask();
        assertEquals("Task C", undone1.getDescription(),
                "Should undo last executed task (C)");

        Task undone2 = taskManager.undoLastTask();
        assertEquals("Task B", undone2.getDescription(),
                "Should undo second-to-last task (B)");

        Task undone3 = taskManager.undoLastTask();
        assertEquals("Task A", undone3.getDescription(),
                "Should undo first executed task (A)");

        // Verify all tasks are back in queue
        assertEquals(3, taskManager.remainingTaskCount());
        assertEquals(0, taskManager.completedTaskCount());
    }

    @Test
    public void testUndoWithNoCompletedTasks() {
        // Attempt to undo with no completed tasks
        Task undone = taskManager.undoLastTask();

        assertNull(undone, "Should return null when no tasks to undo");
        assertEquals(0, taskManager.remainingTaskCount());
    }

    @Test
    public void testEventSummaryData() {
        // Setup complete event scenario
        List<Guest> generatedGuests = Generators.GenerateGuests(20);
        for (Guest g : generatedGuests) {
            guestManager.addGuest(g);
        }

        selectedVenue = venueSelector.selectVenue(3000, 20);

        taskManager.addTask(new Task("Task 1"));
        taskManager.addTask(new Task("Task 2"));
        taskManager.addTask(new Task("Task 3"));
        taskManager.executeNextTask();

        assertEquals(20, guestManager.getGuestCount(),
                "Summary should show 20 guests");
        assertNotNull(selectedVenue, "Summary should show selected venue");
        assertEquals(2, taskManager.remainingTaskCount(),
                "Summary should show 2 remaining tasks");
        assertEquals(1, taskManager.completedTaskCount(),
                "Summary should show 1 completed task");
    }

    @Test
    public void testMultipleGuestsWithSameGroupTag() {
        // Test that seating handles multiple guests with same group
        for (int i = 1; i <= 10; i++) {
            guestManager.addGuest(new Guest("Family" + i, "family"));
        }

        selectedVenue = venueSelector.selectVenue(5000, 10);
        seatingPlanner = new SeatingPlanner(selectedVenue);
        Map<Integer, List<Guest>> seatingChart =
                seatingPlanner.generateSeating(guestManager.getAllGuests());

        // Verify all family members are seated
        int totalSeated = seatingChart.values().stream()
                .mapToInt(List::size)
                .sum();
        assertEquals(10, totalSeated, "All family members should be seated");
    }

    @Test
    public void testLoadingSampleDataMultipleTimes() {
        // First load
        List<Guest> batch1 = Generators.GenerateGuests(10);
        for (Guest g : batch1) {
            guestManager.addGuest(g);
        }
        assertEquals(10, guestManager.getGuestCount());

        List<Guest> batch2 = Generators.GenerateGuests(5);
        for (Guest g : batch2) {
            guestManager.addGuest(g);
        }
        assertEquals(15, guestManager.getGuestCount(),
                "Should have combined total of guests");
    }

    @Test
    public void testVenueSelectionReturnsLowestCostWhenTied() {
        // Using known venues from Generators
        Venue selected = venueSelector.selectVenue(3000, 30);

        assertNotNull(selected, "Should find a venue");

        // Verify it's the lowest cost that fits
        // From Generators: Community Hall ($1500, 40), Garden Hall ($2500, 60)
        // Both fit 30 guests and $3000 budget, should pick Community Hall
        assertEquals("Community Hall", selected.getName(),
                "Should select lowest cost venue when both fit");
    }

    @Test
    public void testEmptyGuestListSeating() {
        // Attempt to seat with no guests
        selectedVenue = venueSelector.selectVenue(5000, 1);
        seatingPlanner = new SeatingPlanner(selectedVenue);

        Map<Integer, List<Guest>> seatingChart =
                seatingPlanner.generateSeating(guestManager.getAllGuests());

        assertNotNull(seatingChart, "Should return a seating chart");
        assertTrue(seatingChart.isEmpty(),
                "Seating chart should be empty when no guests");
    }
}