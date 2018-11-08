package com.inventage.tools.versiontiger.internal.impl;

import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectId;

/**
 * The id of a {@link Project}.
 *
 * @author Christian Bräm
 */
public class ProjectIdImpl implements ProjectId {

	private static final String UNKNOWN_GROUP = ".UnknownGroupId";
	
    private final String groupId;
    private final String artifactId;

    public static ProjectId createWithUnknownGroup(String artifactId) {
    	return new ProjectIdImpl(UNKNOWN_GROUP, artifactId);
    }
    
    public static ProjectId create(String groupId, String artifactId) {
    	return new ProjectIdImpl(groupId, artifactId);
    }
    
    /**
     * The constructor for project ids.
     *
     * @param groupId the group id.
     * @param artifactId the artifact id. Must not be {@code null}.
     */
    protected ProjectIdImpl(String groupId, String artifactId) {
        if (groupId == null || artifactId == null) {
        	throw new IllegalArgumentException("Illegal project id: groupId=" + groupId + ", artifactId=" + artifactId);
        }
        
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    @Override
    public String getFullId() {
        return groupId + IDENTIFICATION_SEPARATOR + artifactId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }
    
    @Override
    public boolean hasUnknownGroupId() {
    	return UNKNOWN_GROUP.equals(getGroupId());
    }
    
    @Override
    public boolean equalsIgnoreGroupIfUnknown(ProjectId id) {
    	return getArtifactId().equals(id.getArtifactId()) && (hasUnknownGroupId() || id.hasUnknownGroupId() || getGroupId().equals(id.getGroupId()));
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String toString() {
        return getFullId();
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + artifactId.hashCode();
		result = prime * result + groupId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProjectIdImpl other = (ProjectIdImpl) obj;
		if (!artifactId.equals(other.artifactId)) {
			return false;
		}
		if (!groupId.equals(other.groupId)) {
			return false;
		}
		return true;
	}

    @Override
    public int compareTo(ProjectId other) {
    	int result = getGroupId().compareTo(other.getGroupId());
    	
        if (result == 0) {
        	result = getArtifactId().compareTo(other.getArtifactId());
        }
        
        return result;
    }

}
