package demo.project;

import demo.project.event.ProjectEvent;

/**
 * The {@link ProjectStatus} describes the state of an {@link Project}.
 * The aggregate state of a {@link Project} is sourced from attached domain
 * events in the form of {@link ProjectEvent}.
 *
 * @author kbastani
 */
public enum ProjectStatus {
    PROJECT_CREATED,
    PROJECT_SUSPENDED
}
