import edu.course.eventplanner.model.Venue;
import edu.course.eventplanner.service.VenueSelector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testLowestCost_ShouldBeChosen_WhenCapacityIsTied () {
        Venue venue1 = new Venue("Big Room",5000,120,15,8);
        Venue venue2 = new Venue("Small Room",5000,50,15,8);

        VenueSelector venueSelector = new VenueSelector(List.of(venue1, venue2));

        int userBudget = 7000;
        int numGuests = 35;

        Venue result = venueSelector.selectVenue(userBudget, numGuests);

        assertEquals("Small Room", result.getName(), "Should pick the smallest capacity that fits when prices are tied.");
    }

    @Test
    public void testSelectVenue_ShouldAllow_WhenGuestsExactlyEqualCapacity ()
    {
        Venue venue = new  Venue("Big Room",5000,120,15,8);
        VenueSelector venueSelector = new VenueSelector(List.of(venue));

        int userBudget = 6000;
        int numGuests = 120;

        Venue result = venueSelector.selectVenue(userBudget, numGuests);
        assertNotNull(result, "Should accept venue when guests equal max capacity");
    }

    @Test
    public void testSelectVenue_ShouldPickCheapest_WhenMultipleVenuesAreValid(){
        Venue expensive = new Venue("Expensive",5000,120,15,8);
        Venue cheaper = new Venue("Cheaper",3000,80,10,8);

        VenueSelector venueSelector = new VenueSelector(List.of(expensive, cheaper));

        int userBudget = 6000;
        int numGuests = 70;

        Venue result = venueSelector.selectVenue(userBudget, numGuests);

        assertNotNull(result, "A venue should have been picked.");
        assertEquals("Cheaper", result.getName(), "Should pick the cheapest option if more then one work with budget." );
    }

}
// needs to check that venue is valid, -
// if cost is greater than budget it should not return that venue -
// if capacity is greater than the venues capacity then it should not return that venue
// the best venue has lowest cost - tied smallest capacity that fits wins
