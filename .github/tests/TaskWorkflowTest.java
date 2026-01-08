package edu.course.eventplanner;

import edu.course.eventplanner.model.Task;
import edu.course.eventplanner.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Instructor-provided tests for TaskManager
 * Tests queue execution and stack-based undo functionality
 */
public class TaskWorkflowTest {

    private TaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = new TaskManager();
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Send invitations");
        manager.addTask(task);

        assertEquals(1, manager.remainingTaskCount(), "Should have 1 task in queue");
    }

    @Test
    public void testExecuteNextTask() {
        Task task1 = new Task("Book venue");
        Task task2 = new Task("Send invitations");

        manager.addTask(task1);
        manager.addTask(task2);

        Task executed = manager.executeNextTask();
        assertNotNull(executed, "Should execute a task");
        assertEquals("Book venue", executed.getDescription(), "Should execute first task (FIFO)");
        assertEquals(1, manager.remainingTaskCount(), "Should have 1 remaining task");
    }

    @Test
    public void testFIFOOrder() {
        manager.addTask(new Task("Task 1"));
        manager.addTask(new Task("Task 2"));
        manager.addTask(new Task("Task 3"));

        assertEquals("Task 1", manager.executeNextTask().getDescription());
        assertEquals("Task 2", manager.executeNextTask().getDescription());
        assertEquals("Task 3", manager.executeNextTask().getDescription());
    }

    @Test
    public void testUndoLastTask() {
        Task task1 = new Task("Book venue");
        Task task2 = new Task("Send invitations");

        manager.addTask(task1);
        manager.addTask(task2);

        manager.executeNextTask();
        manager.executeNextTask();

        Task undone = manager.undoLastTask();
        assertNotNull(undone, "Should undo the last completed task");
        assertEquals("Send invitations", undone.getDescription(),
            "Should undo most recently completed task (LIFO)");
    }

    @Test
    public void testMultipleUndos() {
        manager.addTask(new Task("Task 1"));
        manager.addTask(new Task("Task 2"));
        manager.addTask(new Task("Task 3"));

        manager.executeNextTask();
        manager.executeNextTask();
        manager.executeNextTask();

        assertEquals("Task 3", manager.undoLastTask().getDescription(), "Undo most recent");
        assertEquals("Task 2", manager.undoLastTask().getDescription(), "Undo second most recent");
        assertEquals("Task 1", manager.undoLastTask().getDescription(), "Undo first task");
    }

    @Test
    public void testUndoWithNoCompletedTasks() {
        Task undone = manager.undoLastTask();
        assertNull(undone, "Should return null when no tasks to undo");
    }

    @Test
    public void testExecuteWithNoTasks() {
        Task executed = manager.executeNextTask();
        assertNull(executed, "Should return null when no tasks to execute");
    }

    @Test
    public void testMixedExecuteAndUndo() {
        manager.addTask(new Task("Task 1"));
        manager.addTask(new Task("Task 2"));
        manager.addTask(new Task("Task 3"));

        manager.executeNextTask(); // Execute Task 1
        manager.executeNextTask(); // Execute Task 2

        manager.undoLastTask(); // Undo Task 2

        assertEquals(2, manager.remainingTaskCount(), "Should have Task 2 and Task 3 remaining");

        Task next = manager.executeNextTask(); // Should execute Task 2
        assertEquals("Task 2", next.getDescription());
        Task last = manager.executeNextTask(); // Should execute Task 3
        assertEquals("Task 3", last.getDescription());
    }

    @Test
    public void testRemainingTaskCount() {
        assertEquals(0, manager.remainingTaskCount(), "Initial count should be 0");

        manager.addTask(new Task("Task 1"));
        manager.addTask(new Task("Task 2"));
        assertEquals(2, manager.remainingTaskCount());

        manager.executeNextTask();
        assertEquals(1, manager.remainingTaskCount());

        manager.executeNextTask();
        assertEquals(0, manager.remainingTaskCount());
    }

    @Test
    public void testStackAndQueueIntegrity() {
        // Test that queue (upcoming) and stack (completed) work correctly together
        manager.addTask(new Task("A"));
        manager.addTask(new Task("B"));
        manager.addTask(new Task("C"));

        manager.executeNextTask(); // A -> completed
        manager.executeNextTask(); // B -> completed

        assertEquals(1, manager.remainingTaskCount(), "Should have C remaining");

        Task undone = manager.undoLastTask(); // B
        assertEquals("B", undone.getDescription());

        undone = manager.undoLastTask(); // A
        assertEquals("A", undone.getDescription());

        undone = manager.undoLastTask(); // Nothing left
        assertNull(undone, "No more tasks to undo");
    }
}



