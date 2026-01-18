import edu.course.eventplanner.model.Task;
import edu.course.eventplanner.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new TaskManager();
    }

    @Test
    public void testAddTask_ShouldIncreaseRemainingCount() {
        Task task = new Task("Send invitations");
        taskManager.addTask(task);

        assertEquals(1, taskManager.remainingTaskCount());
    }

    @Test
    public void testExecuteNextTask_ShouldReturnTask_InFIFOOrder() {
        Task task1 = new Task("Book venue");
        Task task2 = new Task("Order catering");

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        Task executed = taskManager.executeNextTask();

        assertEquals("Book venue", executed.getDescription());
        assertEquals(1, taskManager.remainingTaskCount());
    }

    @Test
    public void testExecuteNextTask_ShouldReturnNull_WhenNoTasks() {
        Task executed = taskManager.executeNextTask();

        assertNull(executed);
    }

    @Test
    public void testUndoLastTask_ShouldRestoreTask() {
        Task task = new Task("Prepare decorations");
        taskManager.addTask(task);
        taskManager.executeNextTask();

        Task undone = taskManager.undoLastTask();

        assertNotNull(undone);
        assertEquals("Prepare decorations", undone.getDescription());
        assertEquals(1, taskManager.remainingTaskCount());
    }

    @Test
    public void testUndoLastTask_ShouldReturnNull_WhenNoCompletedTasks() {
        Task undone = taskManager.undoLastTask();

        assertNull(undone);
    }

    @Test
    public void testExecuteAndUndo_ShouldMaintainCorrectOrder() {
        taskManager.addTask(new Task("Task 1"));
        taskManager.addTask(new Task("Task 2"));

        taskManager.executeNextTask();
        taskManager.executeNextTask();

        Task undone = taskManager.undoLastTask();

        assertEquals("Task 2", undone.getDescription());
        assertEquals(1, taskManager.remainingTaskCount());
    }

    @Test
    public void testTaskCompletion_ShouldMarkTaskAsComplete() {
        Task task = new Task("Complete this");
        taskManager.addTask(task);

        Task executed = taskManager.executeNextTask();

        assertTrue(executed.isComplete());
    }
}
