package edu.course.eventplanner.service;

import edu.course.eventplanner.model.*;
import java.util.*;

public class SeatingPlanner {
    private final Venue venue;
    public SeatingPlanner(Venue venue) { this.venue = venue; }
    public Map<Integer, List<Guest>> generateSeating(List<Guest> guests) {
        if (guests == null || guests.isEmpty()) {
            return new TreeMap<>();
        }

        int seatsPerTable = venue.getSeatsPerTable();

        // Step 1: Group guests by their groupTag using HashMap - O(n)
        Map<String, Queue<Guest>> groupedGuests = new HashMap<>();

        for (Guest guest : guests) {
            String tag = guest.getGroupTag();
            groupedGuests.putIfAbsent(tag, new LinkedList<>());
            groupedGuests.get(tag).offer(guest);
        }

        // Step 2: Use TreeMap (BST) for ordered table storage - O(log n) per insert
        Map<Integer, List<Guest>> seatingChart = new TreeMap<>();

        int currentTable = 1;
        List<Guest> currentTableGuests = new ArrayList<>();

        // Step 3: Process each group and fill tables
        for (Queue<Guest> groupQueue : groupedGuests.values()) {
            while (!groupQueue.isEmpty()) {
                Guest guest = groupQueue.poll();
                currentTableGuests.add(guest);

                // If table is full, move to next table
                if (currentTableGuests.size() == seatsPerTable) {
                    seatingChart.put(currentTable, currentTableGuests);
                    currentTable++;
                    currentTableGuests = new ArrayList<>();
                }
            }
        }

        // Add any remaining guests to the last table
        if (!currentTableGuests.isEmpty()) {
            seatingChart.put(currentTable, currentTableGuests);
        }

        return seatingChart;
    }
}
