package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Venue;
import java.util.*;

public class VenueSelector {
    private final List<Venue> venues;

    public VenueSelector(List<Venue> venues) {
        //this.venues = venues;
        this.venues = venues != null ? new ArrayList<>(venues) : new ArrayList<>();
    }

    public Venue selectVenue(double budget, int guestCount) {
        // Filter valid venues: cost <= budget AND capacity >= guestCount
        List<Venue> validVenues = new ArrayList<>();

        for (Venue venue : venues) {
            if (venue.getCost() <= budget && venue.getCapacity() >= guestCount) {
                validVenues.add(venue);
            }
        }

        // If no valid venues, return null
        if (validVenues.isEmpty()) {
            return null;
        }

        // Sort by cost ascending, then by capacity ascending
        // This uses merge sort (Java's default) - O(n log n)
        Collections.sort(validVenues, new Comparator<Venue>() {
            @Override
            public int compare(Venue v1, Venue v2) {
                // First compare by cost
                int costComparison = Double.compare(v1.getCost(), v2.getCost());
                if (costComparison != 0) {
                    return costComparison;
                }
                // If costs are equal, compare by capacity (prefer smaller)
                return Integer.compare(v1.getCapacity(), v2.getCapacity());
            }
        });

        // Return the first venue (best fit)
        return validVenues.get(0);
    }
}
