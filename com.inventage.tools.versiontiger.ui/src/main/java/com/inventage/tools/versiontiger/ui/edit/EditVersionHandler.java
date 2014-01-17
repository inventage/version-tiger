package com.inventage.tools.versiontiger.ui.edit;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.common.collect.Lists;
import com.inventage.tools.versiontiger.ui.VersioningUIPlugin;
import com.inventage.tools.versiontiger.ui.logger.ConsoleLogger;

public class EditVersionHandler extends AbstractHandler {

	private ConsoleLogger consoleLogger;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		List<IProject> projects = getProjectsFromSelection(event);
		if (!projects.isEmpty()) {
			doModifyVersions(projects);
		}
		return null;
	}

	private void initializeConsole() {
		consoleLogger = new ConsoleLogger();
	}

	private List<IProject> getProjectsFromSelection(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		List<IProject> selectedProjects = Lists.newArrayList();
		if (selection instanceof IStructuredSelection) {
			@SuppressWarnings("unchecked")
			Iterator<Object> structuredSelectionIterator = ((IStructuredSelection) selection).iterator();
			while (structuredSelectionIterator.hasNext()) {
				Object element = structuredSelectionIterator.next();
				IProject project = (IProject) Platform.getAdapterManager().getAdapter(element, IProject.class);
				if (project != null) {
					selectedProjects.add(project);
				}
			}
		}

		return selectedProjects;
	}

	private void doModifyVersions(List<IProject> selectedProjects) throws ExecutionException {
		initializeConsole();
		
		EditVersionModel editVersionModel = getVersionModelToApply(selectedProjects);

		if (editVersionModel != null) {
			applyNewVersions(editVersionModel);
			try {
				ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IWorkspaceRoot.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				throw new ExecutionException("Failed to refresh Workspace", e);
			}
		}
	}

	private EditVersionModel getVersionModelToApply(List<IProject> selectedProjects) {
		try {
			EditVersionWizard wizard = new EditVersionWizard(selectedProjects, consoleLogger);
			WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);

			if (wizardDialog.open() == WizardDialog.OK) {
				return wizard.getEditVersionModel();
			}
		} catch (Exception e) {
			StatusManager.getManager().handle(new Status(Status.ERROR, VersioningUIPlugin.PLUGIN_ID, "Cannot display current project version state.", e),
					StatusManager.SHOW);
		}
		return null;
	}

	private void applyNewVersions(EditVersionModel editVersionModel) {
		try {
			editVersionModel.applyNewVersions();
			consoleLogger.close();
			
		} catch (Exception e) {
			StatusManager.getManager().handle(new Status(Status.ERROR, VersioningUIPlugin.PLUGIN_ID, "Error occurred while applying new versions.", e),
					StatusManager.SHOW);
		}
	}

}
