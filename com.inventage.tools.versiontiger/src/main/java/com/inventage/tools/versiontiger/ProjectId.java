package com.inventage.tools.versiontiger;

/**
 * The project id.
 * <p>It is a composite id from its group id and artifact id.</p>
 *
 * @author Christian Bräm
 */
public interface ProjectId extends Comparable<ProjectId> {

    /**
     * This separator should be used to concatenate the composite key.
     */
    String IDENTIFICATION_SEPARATOR = ":";

    String getFullId();

    String getGroupId();

    String getArtifactId();
    
    boolean hasUnknownGroupId();
    
    boolean equalsIgnoreGroupIfUnknown(ProjectId id);
    
}
