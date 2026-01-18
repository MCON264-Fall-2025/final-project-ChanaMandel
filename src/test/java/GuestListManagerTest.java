
import edu.course.eventplanner.model.Guest;
import edu.course.eventplanner.service.GuestListManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GuestListManagerTest {
    private GuestListManager manager;

    @BeforeEach
    public void setUp() {
        manager = new GuestListManager();
    }

    @Test
    public void testAddGuest_ShouldIncreaseCount() {
        Guest guest = new Guest("Alice", "family");
        manager.addGuest(guest);

        assertEquals(1, manager.getGuestCount());
    }

    @Test
    public void testAddGuest_ShouldBeAccessible() {
        Guest guest = new Guest("Bob", "friends");
        manager.addGuest(guest);

        Guest found = manager.findGuest("Bob");
        assertNotNull(found);
        assertEquals("Bob", found.getName());
    }

    @Test
    public void testRemoveGuest_ShouldReturnTrue_WhenGuestExists() {
        Guest guest = new Guest("Charlie", "coworkers");
        manager.addGuest(guest);

        boolean removed = manager.removeGuest("Charlie");

        assertTrue(removed);
        assertEquals(0, manager.getGuestCount());
    }

    @Test
    public void testRemoveGuest_ShouldReturnFalse_WhenGuestDoesNotExist() {
        boolean removed = manager.removeGuest("NonExistent");

        assertFalse(removed);
    }

    @Test
    public void testFindGuest_ShouldReturnNull_WhenGuestDoesNotExist() {
        Guest found = manager.findGuest("Unknown");

        assertNull(found);
    }

    @Test
    public void testGetAllGuests_ShouldReturnAllAddedGuests() {
        manager.addGuest(new Guest("Alice", "family"));
        manager.addGuest(new Guest("Bob", "friends"));
        manager.addGuest(new Guest("Charlie", "neighbors"));

        List<Guest> allGuests = manager.getAllGuests();

        assertEquals(3, allGuests.size());
    }

    @Test
    public void testRemoveGuest_ShouldMaintainConsistency() {
        manager.addGuest(new Guest("Alice", "family"));
        manager.addGuest(new Guest("Bob", "friends"));

        manager.removeGuest("Alice");

        assertNull(manager.findGuest("Alice"));
        assertNotNull(manager.findGuest("Bob"));
        assertEquals(1, manager.getGuestCount());
    }
}
// needs to be able to add a guest
// remove a guest
// find a guest
// print all the guests