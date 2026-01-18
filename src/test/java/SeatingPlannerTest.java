import edu.course.eventplanner.model.Guest;
import edu.course.eventplanner.model.Venue;
import edu.course.eventplanner.service.SeatingPlanner;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeatingPlannerTest {
    @Test
    public void testGenerateSeating_ShouldGroupGuestsByTag() {
        Venue venue = new Venue("Test Hall", 5000, 100, 10, 4);
        SeatingPlanner planner = new SeatingPlanner(venue);

        List<Guest> guests = Arrays.asList(
                new Guest("Alice", "family"),
                new Guest("Bob", "family"),
                new Guest("Charlie", "friends"),
                new Guest("Diana", "friends")
        );

        Map<Integer, List<Guest>> seating = planner.generateSeating(guests);

        assertEquals(1, seating.size());
        assertEquals(4, seating.get(1).size());
    }

    @Test
    public void testGenerateSeating_ShouldFillTablesCompletely() {
        Venue venue = new Venue("Small Room", 3000, 50, 5, 2);
        SeatingPlanner planner = new SeatingPlanner(venue);

        List<Guest> guests = Arrays.asList(
                new Guest("Alice", "family"),
                new Guest("Bob", "family"),
                new Guest("Charlie", "friends"),
                new Guest("Diana", "friends"),
                new Guest("Eve", "neighbors")
        );

        Map<Integer, List<Guest>> seating = planner.generateSeating(guests);

        assertEquals(3, seating.size());
        assertEquals(2, seating.get(1).size());
        assertEquals(2, seating.get(2).size());
        assertEquals(1, seating.get(3).size());
    }

    @Test
    public void testGenerateSeating_ShouldReturnEmpty_WhenNoGuests() {
        Venue venue = new Venue("Empty Hall", 5000, 100, 10, 4);
        SeatingPlanner planner = new SeatingPlanner(venue);

        Map<Integer, List<Guest>> seating = planner.generateSeating(new ArrayList<>());

        assertTrue(seating.isEmpty());
    }

    @Test
    public void testGenerateSeating_ShouldUseOrderedTableNumbers() {
        Venue venue = new Venue("Big Hall", 8000, 200, 20, 3);
        SeatingPlanner planner = new SeatingPlanner(venue);

        List<Guest> guests = Arrays.asList(
                new Guest("A", "g1"),
                new Guest("B", "g1"),
                new Guest("C", "g1"),
                new Guest("D", "g2"),
                new Guest("E", "g2"),
                new Guest("F", "g2")
        );

        Map<Integer, List<Guest>> seating = planner.generateSeating(guests);

        List<Integer> tableNumbers = new ArrayList<>(seating.keySet());
        assertEquals(Arrays.asList(1, 2), tableNumbers);
    }
}
