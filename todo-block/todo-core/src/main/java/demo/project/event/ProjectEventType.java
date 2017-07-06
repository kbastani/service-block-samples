package demo.project.event;

import demo.project.Project;
import demo.project.ProjectStatus;

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
