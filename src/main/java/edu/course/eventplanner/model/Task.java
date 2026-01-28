package edu.course.eventplanner.model;

//public class Task {
//    private final String description;
//    public Task(String description) { this.description = description; }
//    public String getDescription() { return description; }
//}
public class Task {
    private final String description;
    private boolean complete;

    public Task(String description) {
        this.description = description;
        this.complete = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isComplete() {
        return complete;
    }

    public void markComplete() {
        this.complete = true;
    }

    public void markIncomplete() {
        this.complete = false;
    }

    @Override
    public String toString() {
        return description + (complete ? " [COMPLETED]" : " [PENDING]");
    }
}
