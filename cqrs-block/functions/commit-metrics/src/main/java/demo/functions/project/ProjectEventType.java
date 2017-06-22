package demo.functions.project;

/**
 * The {@link ProjectEventType} represents a collection of possible events that describe
 * state transitions of {@link ProjectStatus} on the {@link Project} aggregate.
 *
 * @author kbastani
 */
public enum ProjectEventType {
    CREATED_EVENT,
    COMMIT_EVENT
}
