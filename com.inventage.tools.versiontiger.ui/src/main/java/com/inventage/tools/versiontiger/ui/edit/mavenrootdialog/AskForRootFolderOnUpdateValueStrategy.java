package com.inventage.tools.versiontiger.ui.edit.mavenrootdialog;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitions;

/**
 * Update value strategy which works on {@link ProjectUniverse}s and only serves
 * those of type maven root project universe. On update, a dialog is opened
 * asking for a root directory. The result is fed into the universe.
 */
public class AskForRootFolderOnUpdateValueStrategy extends UpdateValueStrategy {

	public AskForRootFolderOnUpdateValueStrategy(Shell shell) {
		setConverter(createShowDialogConverter(shell));
	}

	private IConverter createShowDialogConverter(final Shell shell) {
		return new IConverter() {

			@Override
			public Object getToType() {
				return ProjectUniverse.class;
			}

			@Override
			public Object getFromType() {
				return ProjectUniverse.class;
			}

			@Override
			public Object convert(Object fromObject) {
				ProjectUniverse projectUniverse = (ProjectUniverse) fromObject;
				if (projectUniverse.id().equals(UniverseDefinitions.MAVEN_ROOT_PROJECTS_UNIVERSE_ID)) {

					DirectoryDialog directoryDialog = new DirectoryDialog(shell);
					directoryDialog.setText(Messages.rootPathDialogTitle);
					directoryDialog.setMessage(Messages.rootPathDialogMessage);

					String directory = directoryDialog.open();
					if (directory != null) {
						projectUniverse.clearProjects();
						projectUniverse.addRootProjectPath(directory);
					}
				}
				return projectUniverse;
			}
		};
	}

}
