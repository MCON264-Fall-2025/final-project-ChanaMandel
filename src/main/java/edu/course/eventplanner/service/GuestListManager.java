package edu.course.eventplanner.service;

import edu.course.eventplanner.model.Guest;
import java.util.*;

public class GuestListManager {
    private final LinkedList<Guest> guests = new LinkedList<>();
    private final Map<String, Guest> guestByName = new HashMap<>();

    public void addGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("Guest cannot be null");
        }
        guests.add(guest);
        guestByName.put(guest.getName(), guest);
    }
    public boolean removeGuest(String guestName) {
        if (guestName == null) {
            return false;
        }
        Guest guest = guestByName.get(guestName);
        if (guest == null) {
            return false;
        }
        guestByName.remove(guestName);
        guests.remove(guest);
        return true;
    }
    public Guest findGuest(String guestName) {
        if (guestName == null) {
            return null;
        }
        return guestByName.get(guestName);
    }
    public int getGuestCount() { return guests.size(); }
    public List<Guest> getAllGuests() { return guests; }
}
