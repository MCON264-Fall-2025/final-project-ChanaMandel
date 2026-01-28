# Event Planner Mini

This project demonstrates practical use of data structures:
linked lists, stacks, queues, maps, trees, sorting, and searching.

## What You Must Do
- Implement all TODO methods
- Write JUnit 5 tests for core logic
- Pass instructor autograding tests
- Explain your design choices in this README

See Canvas assignment for full requirements.

# Event Planning System

## Project Overview
This is a console-based Java application that manages small event planning using data structures. 
The system handles venue selection, guest management, seating arrangements, and task workflow with undo capabilities.

### 1. **LinkedList** - Guest List (Source of Truth)
**Where Used:** `GuestListManager.guests`

**Why:** The linked list serves as the authoritative master guest list. It maintains insertion order and allows efficient additions and removals from any position. The assignment specifies that the guest list must use a linked list as the source of truth.

**Operations:**
- Add guest: O(1) - append to end
- Remove guest: O(n) - must find element first
- Iterate all: O(n)

---

### 2. **HashMap** - Fast Guest Lookup
**Where Used:** `GuestListManager.guestByName`

**Why:** While the linked list is the source of truth, a HashMap provides O(1) average-case lookup by guest name. This dramatically improves performance when finding or removing guests by name.

**Operations:**
- Find guest by name: O(1) average case
- Add to map: O(1) average case
- Remove from map: O(1) average case

**Design Pattern:** The HashMap is kept synchronized with the LinkedList. Every add/remove operation updates both structures to maintain consistency.

---

### 3. **Queue** (LinkedList implementation) - Task Execution
**Where Used:** `TaskManager.upcoming`

**Why:** Tasks must be executed in FIFO (First-In-First-Out) order. A queue naturally supports this requirement with `offer()` and `poll()` operations.

**Operations:**
- Add task: O(1)
- Execute next: O(1)
- Check size: O(1)

---

### 4. **Stack** - Task Undo
**Where Used:** `TaskManager.completed`

**Why:** Undo functionality requires LIFO (Last-In-First-Out) behavior. When undoing, we want to reverse the most recently completed task. Stack's `push()` and `pop()` operations naturally support this.

**Operations:**
- Push completed task: O(1)
- Pop for undo: O(1)
- Check if empty: O(1)

---

### 5. **TreeMap** (BST-based) - Ordered Seating Chart
**Where Used:** `SeatingPlanner.generateSeating()` return value

**Why:** A TreeMap (implemented as a Red-Black Tree, a type of balanced BST) maintains table numbers in sorted order automatically. This ensures the seating chart displays tables in numerical sequence (1, 2, 3...).

**Operations:**
- Insert table: O(log n)
- Retrieve in order: O(n)
- Access by table number: O(log n)

**Alternative Considered:** A regular HashMap would work but wouldn't guarantee order. TreeMap provides both the map functionality and ordered keys.

---

### 6. **HashMap** - Grouping Guests by Tag
**Where Used:** `SeatingPlanner.generateSeating()` - grouping phase

**Why:** We need to efficiently group guests by their `groupTag` (family, friends, etc.). HashMap allows O(1) insertion and lookup, making the grouping phase efficient.

**Operations:**
- Group guest by tag: O(1) per guest
- Total grouping: O(n) where n = number of guests

---

### 7. **Queue** (LinkedList) - Fair Guest Distribution
**Where Used:** `SeatingPlanner` - per-group queues

**Why:** After grouping guests, we need to distribute them to tables fairly. Using a queue for each group ensures guests from the same group sit together and are processed in order.

**Operations:**
- Add guest to group queue: O(1)
- Poll guest for seating: O(1)

---

## Algorithms Used

### Sorting Algorithm: Merge Sort (via Collections.sort)
**Where Used:** `VenueSelector.selectVenue()`

**Purpose:** Sort valid venues by cost (ascending), then by capacity (ascending) to find the best venue.

**Complexity:** O(n log n) where n = number of valid venues

**Why This Algorithm:** Java's `Collections.sort()` uses a modified merge sort (TimSort) which is stable and performs well on partially sorted data. Since we need stable sorting (maintaining relative order when costs are equal), this is ideal.

**Implementation Details:**
1. Filter venues that meet budget and capacity constraints: O(n)
2. Sort filtered venues using custom comparator: O(m log m) where m ≤ n
3. Return the first venue (best fit): O(1)

---

### Search Algorithm: Hash-based Search
**Where Used:** `GuestListManager.findGuest()`

**Purpose:** Find a guest by name quickly.

**Complexity:** O(1) average case

**Why This Algorithm:** HashMap provides the fastest lookup for exact-match searches. Since we're searching by exact name (not range or pattern), hashing is optimal.

---

## Big-O Complexity Analysis

### Finding a Guest
**Operation:** `GuestListManager.findGuest(String name)`

**Complexity:** **O(1)** average case

**Explanation:** Uses HashMap lookup which provides constant-time access. The underlying hash function distributes keys evenly, and with low load factor, collisions are rare.

**Worst Case:** O(n) if all guests hash to the same bucket (extremely unlikely with Java's String hashCode)

---

### Selecting a Venue
**Operation:** `VenueSelector.selectVenue(double budget, int guestCount)`

**Complexity:** **O(n log n)** where n = total number of venues

**Breakdown:**
1. Filter valid venues: O(n) - iterate through all venues
2. Sort valid venues: O(m log m) where m = number of valid venues
3. Select first venue: O(1)

**Dominant Term:** O(n log n) considering worst case where all venues are valid (m = n)

**Space Complexity:** O(n) for storing valid venues list

---

### Generating Seating Chart
**Operation:** `SeatingPlanner.generateSeating(List<Guest> guests)`

**Complexity:** **O(n log t)** where n = number of guests, t = number of tables

**Breakdown:**
1. Group guests by tag: O(n) - iterate through all guests
2. Create queues: O(g) where g = number of unique groups (g ≤ n)
3. Distribute to tables: O(n) - process each guest once
4. Insert into TreeMap: O(log t) per table, total O(t log t)

**Dominant Term:** O(n log t) where typically t << n

**Space Complexity:** O(n) for storing guests in queues and tables

---

## Class Design

### GuestListManager
**Responsibility:** Manages the master guest list and provides fast lookup

**Key Design Decisions:**
- Maintains both LinkedList (source of truth) and HashMap (fast access)
- All guests must be added via `addGuest()` method
- Synchronizes both data structures on every operation

**Invariant:** Both data structures always contain the exact same guests

**Public Methods:**
- `addGuest(Guest)` - O(1)
- `removeGuest(String)` - O(1) lookup + O(n) removal
- `findGuest(String)` - O(1)
- `getGuestCount()` - O(1)
- `getAllGuests()` - O(n)

---

### VenueSelector
**Responsibility:** Finds the best venue given budget and capacity constraints

**Key Design Decisions:**
- Filters venues first, then sorts to find optimal venue
- Uses two-level sorting: primary by cost, secondary by capacity
- Returns null if no venue meets requirements

**Selection Criteria:**
1. Cost ≤ budget
2. Capacity ≥ guest count
3. Among valid venues: lowest cost
4. If tied on cost: smallest capacity that still fits

**Public Methods:**
- `selectVenue(double, int)` - O(n log n)

---

### SeatingPlanner
**Responsibility:** Creates seating chart grouping guests by common features

**Key Design Decisions:**
- Two-phase approach: group by tag, then distribute to tables
- Uses TreeMap for ordered table display
- Fills tables sequentially to maximize space utilization

**Algorithm:**
1. Group guests by `groupTag` using HashMap
2. Create a Queue for each group
3. Fill tables one at a time from group queues
4. Store in TreeMap for ordered output

**Public Methods:**
- `generateSeating(List<Guest>)` - O(n log t)

---

### TaskManager
**Responsibility:** Manages task workflow with undo capability

**Key Design Decisions:**
- Separate Queue for upcoming tasks (FIFO)
- Separate Stack for completed tasks (LIFO)
- Tasks track completion state

**Workflow:**
- Execute: Poll from queue → mark complete → push to stack
- Undo: Pop from stack → mark incomplete → add to front of queue

**Public Methods:**
- `addTask(Task)` - O(1)
- `executeNextTask()` - O(1)
- `undoLastTask()` - O(1)
- `remainingTaskCount()` - O(1)
- `completedTaskCount()` - O(1)

---

## Testing Strategy

### Unit Tests Cover:

#### GuestListManager (GuestListManagerTest.java)
- Adding guests (normal + edge cases)
- Removing guests (exists + doesn't exist)
- Finding guests (exists + doesn't exist)
- Getting all guests
- Maintaining consistency between data structures

#### VenueSelector (VenueSelectorTest.java)
- Budget constraints (over/under/exact)
- Capacity constraints (over/under/exact)
- Tie-breaking rules (lowest cost, smallest capacity)
- Multiple valid venues
- No valid venues (return null)

#### SeatingPlanner (SeatingPlannerTest.java)
- Grouping guests by tag
- Filling tables to capacity
- Handling empty guest lists
- Multiple tables
- Ordered table numbers (BST property)

#### TaskManager (TaskManagerTest.java)
- Adding tasks
- FIFO execution order
- LIFO undo order
- Empty state handling
- Task completion status

---

## Edge Cases Tested

- **Null inputs** - All methods handle null gracefully
- **Empty collections** - Operations on empty lists/queues/stacks
- **Boundary conditions** - Exact budget match, exact capacity match
- **Tie-breaking** - Multiple venues with same cost or capacity
- **State consistency** - LinkedList and HashMap stay synchronized

---

## Project Structure

```
src/
├── edu/course/eventplanner/
│   ├── User.java                    # Main console application
│   ├── model/
│   │   ├── Guest.java               # Guest data model
│   │   ├── Venue.java               # Venue data model
│   │   └── Task.java                # Task data model
│   ├── service/
│   │   ├── GuestListManager.java   # Guest management
│   │   ├── VenueSelector.java      # Venue selection logic
│   │   ├── SeatingPlanner.java     # Seating algorithm
│   │   └── TaskManager.java        # Task workflow
│   └── util/
│       └── Generators.java          # Helper data generation
test/
└── edu/course/eventplanner/
    ├── GuestListManagerTest.java
    ├── VenueSelectorTest.java
    ├── SeatingPlannerTest.java
    └── TaskManagerTest.java
```

---

## Running the Application

### Compile
```bash
javac -d bin src/edu/course/eventplanner/**/*.java
```

### Run Tests (with JUnit)
```bash
java -cp bin:junit-platform-console-standalone.jar \
  org.junit.platform.console.ConsoleLauncher \
  --scan-classpath
```

### Run Application
```bash
java -cp bin edu.course.eventplanner.User
```

---

## Sample Application Flow

1. **Load Sample Data** - Generates venues and guests
2. **Select Venue** - Enter budget, system finds best venue
3. **Generate Seating** - Groups guests by tag, assigns tables
4. **Manage Tasks** - Add, execute, and undo preparation tasks
5. **Print Summary** - View complete event overview

---

## Performance Characteristics Summary

| Operation | Data Structure | Time Complexity | Space Complexity |
|-----------|---------------|-----------------|------------------|
| Add guest | LinkedList + HashMap | O(1) | O(n) |
| Find guest | HashMap | O(1) avg | O(n) |
| Remove guest | LinkedList + HashMap | O(n) | O(n) |
| Select venue | ArrayList + Sort | O(n log n) | O(n) |
| Generate seating | HashMap + TreeMap | O(n log t) | O(n) |
| Execute task | Queue | O(1) | O(n) |
| Undo task | Stack | O(1) | O(n) |

---

## Design Principles Applied

1. **Separation of Concerns** - Each class has a single, well-defined responsibility
2. **Data Structure Synchronization** - LinkedList and HashMap stay consistent
3. **Immutability** - Model classes use final fields where possible
4. **Null Safety** - All public methods handle null inputs gracefully
5. **Testability** - Pure logic classes with no I/O dependencies
6. **Clear Contracts** - Method signatures match autograder requirements exactly


