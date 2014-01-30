package com.inventage.tools.versiontiger.ui.edit;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import com.inventage.tools.versiontiger.VersioningLogger;

public class EditVersionWizard extends Wizard {

	private EditVersionModel editVersionModel;

	EditVersionWizard(Collection<IProject> selectedProjects, VersioningLogger logger) {
		setWindowTitle(Messages.editVersionWizardPageTitle);
		this.editVersionModel = new EditVersionModel(logger, selectedProjects);
	}

	@Override
	public void addPages() {
		addPage(new EditVersionPage(editVersionModel));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	EditVersionModel getEditVersionModel() {
		return editVersionModel;
	}

}
