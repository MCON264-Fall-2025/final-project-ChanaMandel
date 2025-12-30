import edu.course.eventplanner.model.Venue;
import edu.course.eventplanner.service.VenueSelector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

public class VenueSelectorTest
{
    @Test
    public void testSelectVenue_ShouldRejectVenue_WhenCostExceedsBudget ()
    {
        Venue expensiveVenue = new Venue("Expensive",10000,120,15,8);
        VenueSelector venueSelector = new VenueSelector(List.of(expensiveVenue));

        int userBudget = 5000;
        int numGuests = 50;

        Venue result = venueSelector.selectVenue(userBudget, numGuests);

        assertNull(result, "The selector should return null if the budget is too low");
    }

    @Test
    public void testSelectVenue_ShouldRejectVenue_WhenGuestCountExceedsCapacity ()
    {
        Venue smallVenue = new Venue("Small Venue",5000,50,15,8);
        VenueSelector venueSelector = new VenueSelector(List.of(smallVenue));

        int userBudget = 5000;
        int numGuests = 70;

        Venue result = venueSelector.selectVenue(userBudget, numGuests);

        assertNull(result, "The selector should return null if the capacity is too low");
    }
}
// needs to check that venue is valid,
// if cost is less than budget it should not return that venue
// if capacity is greater than the venues capacity then it should not return that venue
// the best venue has lowest cost - tied smallest capacity that fits wins
