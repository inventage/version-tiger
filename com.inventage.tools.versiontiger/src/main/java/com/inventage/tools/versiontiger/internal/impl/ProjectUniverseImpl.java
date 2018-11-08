package com.inventage.tools.versiontiger.internal.impl;

import static com.inventage.tools.versiontiger.ProjectId.IDENTIFICATION_SEPARATOR;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.MavenVersion;
import com.inventage.tools.versiontiger.Project;
import com.inventage.tools.versiontiger.ProjectId;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.Versionable;
import com.inventage.tools.versiontiger.VersioningLogger;
import com.inventage.tools.versiontiger.VersioningLoggerItem;
import com.inventage.tools.versiontiger.VersioningLoggerStatus;
import com.inventage.tools.versiontiger.util.FileHandler;

class ProjectUniverseImpl implements ProjectUniverse {

	private static final Pattern SEPARATOR_PATTERN = Pattern.compile("[^(]:");

	private final Map<ProjectId, Project> projects = new TreeMap<ProjectId, Project>();
	private final String id;
	private final String name;
	private final ProjectFactory projectFactory;
	private final VersioningLogger logger;

	ProjectUniverseImpl(String id, ProjectFactory projectFactory, VersioningLogger logger) {
		this(id, null, projectFactory, logger);
	}

	ProjectUniverseImpl(String id, String name, ProjectFactory projectFactory, VersioningLogger logger) {
		this.id = id;
		this.name = name;
		this.projectFactory = projectFactory;

		this.logger = logger;
	}

	@Override
	public String id() {
		return id;
	}

	public String name() {
		return name;
	}

	@Override
	public Project createProjectFromPath(String projectRootFilePath) {
		return projectFactory.createProjectFromRootFilePath(projectRootFilePath, logger);
	}

	@Override
	public void addProject(Project project) {
		if (project != null) {
			projects.put(project.id(), project);
		}
	}

	@Override
	public Project addProjectPath(String projectRootFilePath) {
		Project project = projectFactory.createProjectFromRootFilePath(projectRootFilePath, logger);

		if (project != null && project.exists()) {
			if (projects.containsKey(project.id())) {
				VersioningLoggerItem logItem = logger.createVersioningLoggerItem();
				logItem.setStatus(VersioningLoggerStatus.WARNING);
				Project existingProject = projects.get(project.id());
				logItem.setProject(existingProject);
				logItem.appendToMessage("Removing project from universe (id conflict): " + existingProject.projectPath());
				logger.addVersioningLoggerItem(logItem);
			}

			projects.put(project.id(), project);
		}

		return project;
	}

	@Override
	public Set<Project> addRootProjectPath(String projectRootFilePath) {
		Set<Project> recursiveProjects = projectFactory.createRecursiveProjectsFromRootFilePath(projectRootFilePath, logger);
		Set<Project> result = new HashSet<Project>();

		for (Project project: recursiveProjects) {
			projects.put(project.id(), project);
			result.add(project);
		}

		return result;
	}

	@Override
	public ProjectId idForProjectPath(String projectPath) {
		File canonicalProjectPath = new FileHandler().getCanonicalFile(new File(projectPath));

		for (Project project : projects.values()) {
			if (canonicalProjectPath.equals(new File(project.projectPath()))) {
				return project.id();
			}
		}
		return null;
	}

	@Override
	public void removeProject(String projectId) {
		removeProject(createProjectIdQuery(projectId));
	}

	@Override
	public void removeProject(ProjectId projectId) {
		if (projectId != null) {
			projects.remove(projectId);
		}
	}

	@Override
	public Set<Project> removeProjectsInPath(String path) {
		FileHandler fileHandler = new FileHandler();
		String matcherPath = fileHandler.getCanonicalPath(new File(path));

		Set<Project> result = new HashSet<Project>();
		for (Project project : new ArrayList<Project>(projects.values())) {
			String currentPath = fileHandler.getCanonicalPath(new File(project.projectPath()));
			if (currentPath.startsWith(matcherPath)) {
				projects.remove(project.id());
				result.add(project);

				VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
				loggerItem.setStatus(VersioningLoggerStatus.SUCCESS);
				loggerItem.appendToMessage("Removed project: " + project.id());
				logger.addVersioningLoggerItem(loggerItem);
			}
		}

		return result;
	}

	public Set<Project> listAllProjects() {
		Set<Project> result = new LinkedHashSet<Project>();

		for (Project project : projects.values()) {
			result.add(new ReferencesUpdater(project, this, logger));
		}

		return result;
	}

	@Override
	public Project getProjectWithId(String projectId) {
		return getProjectWithId(createProjectIdQuery(projectId));
	}

	@Override
	public Project getProjectWithId(ProjectId projectId) {
		Project project = projects.get(projectId);
		if (project != null) {
			return new ReferencesUpdater(project, this, logger);
		}
		return null;
	}

	@Override
	public Versionable getAllProjects() {
		return new MultiProjectVersionable(listAllProjects());
	}

	@Override
	public Versionable getAllProjectsWithMatchingIdPattern(String projectIdPattern) {
		Set<Project> result = new LinkedHashSet<Project>();

		final Pattern pattern;
		final boolean legacyPattern;

		if (! SEPARATOR_PATTERN.matcher(projectIdPattern).find()) {
			// make old version compatible
			final String newPatternString = ".+" + IDENTIFICATION_SEPARATOR + projectIdPattern;
			pattern = Pattern.compile(newPatternString);
			legacyPattern = true;
		} else {
			pattern = Pattern.compile(projectIdPattern);
			legacyPattern = false;
		}

		for (Project project : listAllProjects()) {
			if (pattern.matcher(project.id().getFullId()).matches()) {
				result.add(project);
			}
		}

		if (result.isEmpty()) {
			VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
			loggerItem.setStatus(VersioningLoggerStatus.WARNING);
			loggerItem.appendToMessage("No project matched the pattern: " + pattern);
			logger.addVersioningLoggerItem(loggerItem);
		}
		else if (legacyPattern && result.size() > 1 && hasProjectsWithSameArtifactId()) {
			VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
			loggerItem.setStatus(VersioningLoggerStatus.WARNING);
			loggerItem.appendToMessage("The pattern '" + projectIdPattern + "' may match too many projects since the pattern doesn't include a group id pattern "
					+ "and there are multiple projects with the same artifact id. "
					+ "A fully specified project id pattern looks like this: \"groupId" + IDENTIFICATION_SEPARATOR + "artifactId\".");
			logger.addVersioningLoggerItem(loggerItem);
		}
		
		return new MultiProjectVersionable(result);
	}

	@Override
	public void updateReferencesFor(String id, MavenVersion oldVersion, MavenVersion newVersion) {
		updateReferencesFor(createProjectIdQuery(id), oldVersion, newVersion);
	}
	
	@Override
	public void updateReferencesFor(ProjectId id, MavenVersion oldVersion, MavenVersion newVersion) {
		for (Project project : listAllProjects()) {
			project.updateReferencesFor(id, oldVersion, newVersion, this);
		}
	}

	@Override
	public boolean ensureStrictOsgiDependencyTo(String projectId) {
		return ensureStrictOsgiDependencyTo(createProjectIdQuery(projectId));
	}
	
	@Override
	public boolean ensureStrictOsgiDependencyTo(ProjectId projectId) {
		boolean result = true;
		for (Project project : listAllProjects()) {
			if (!project.ensureStrictOsgiDependencyTo(projectId)) {
				result = false;
			}
		}
		return result;
	}
	
	private boolean hasProjectsWithSameArtifactId() {
		Set<String> artifactIds = new HashSet<String>();
		
		for (ProjectId projectId : projects.keySet()) {
			if (artifactIds.contains(projectId.getArtifactId())) {
				return true;
			}
			artifactIds.add(projectId.getArtifactId());
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	private ProjectId createProjectIdQuery(String projectIdQuery) {
        if (projectIdQuery == null) {
            throw new IllegalArgumentException("The project id should not be null");
        }

        String[] ids = projectIdQuery.split(IDENTIFICATION_SEPARATOR, 2);
        if (ids.length == 1) {
            String artifactId = projectIdQuery;
            
            List<ProjectId> similarIds = new LinkedList<ProjectId>();
            for (Project project : listAllProjects()) {
                String projectArtifactId = project.id().getArtifactId();
                if (artifactId.equals(projectArtifactId)) {
                    similarIds.add(project.id());
                }
            }

            if (similarIds.size() > 1) {
                throw new IllegalStateException("Project id (" + projectIdQuery
                		+ ") is not unique: multiple candidates found: " + Arrays.asList(similarIds));
            }
            else if (similarIds.isEmpty()) {
            	VersioningLoggerItem loggerItem = logger.createVersioningLoggerItem();
            	loggerItem.appendToMessage("Project unknown to universe: " + projectIdQuery);
            	loggerItem.setStatus(VersioningLoggerStatus.WARNING);
            	logger.addVersioningLoggerItem(loggerItem);
            	
            	return ProjectIdImpl.createWithUnknownGroup(artifactId);
            }
            return similarIds.get(0);
            
        } else if (ids.length == 2){
            String groupId = ids[0];
            String artifactId = ids[1];
            return ProjectIdImpl.create(groupId, artifactId);
        }
        else {
        	throw new IllegalArgumentException("Invalid project id: " + projectIdQuery);
        }
	}
}
