package com.inventage.tools.versiontiger.universedefinition.ui;

import java.io.File;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.inventage.tools.versiontiger.universedefinition.Messages;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitionPlugin;
import com.inventage.tools.versiontiger.universedefinition.fromfile.FileLocationStore;

public class VersioningPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String UNIVERSE_DEFINITION_FILE_LOCATIONS_PREFERENCE = "universeDefinitionFileLocations"; //$NON-NLS-1$

	private UniverseDefinitionsModel universeDefinitionsModel;
	private TableViewer tableViewer;

	@Override
	public void init(IWorkbench workbench) {
		FileLocationStore store = new FileLocationStore(getPreferenceStore(), UNIVERSE_DEFINITION_FILE_LOCATIONS_PREFERENCE);
		universeDefinitionsModel = new UniverseDefinitionsModel(store.read());
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return UniverseDefinitionPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		Label label = new Label(container, SWT.NONE);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		label.setLayoutData(layoutData);
		label.setText(Messages.projectUniverseSectionTitle);
		tableViewer = createUniverseTable(container);
		createAddRemoveButtons(container);

		return container;
	}

	private TableViewer createUniverseTable(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.NONE);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn nameCol = new TableViewerColumn(tableViewer, SWT.NONE);
		nameCol.getColumn().setWidth(200);
		nameCol.getColumn().setText(Messages.projectUniverseNamePropertyLabel);

		TableViewerColumn locationCol = new TableViewerColumn(tableViewer, SWT.NONE);
		locationCol.getColumn().setWidth(400);
		locationCol.getColumn().setText(Messages.projectUniverseFilePropertyLabel);

		tableViewer.setSorter(new ViewerSorter());

		IObservableSet input = BeansObservables.observeSet(universeDefinitionsModel, UniverseDefinitionsModel.PN_UNIVERSE_FILES);
		IValueProperty[] labelProperties = BeanProperties.values(new String[] { UniverseFile.PN_NAME, UniverseFile.PN_LOCATION });
		ViewerSupport.bind(tableViewer, input, labelProperties);

		return tableViewer;
	}

	private void createAddRemoveButtons(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));

		Button addButton = new Button(container, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		addButton.setText(Messages.projectUniverseAddButtonText);
		addButton.setToolTipText(Messages.projectUniverseAddButtonTooltip);
		addButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setFilterExtensions(new String[] { "*.xml" });
				fileDialog.setText(Messages.projectUniverseFileSelectionDialogTitle);
				fileDialog.setFileName(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());

				String fileLocation = fileDialog.open();
				if (fileLocation != null) {
					File file = new File(fileLocation);
					universeDefinitionsModel.addFile(file);
					validate();
				}
			}
		});

		Button removeButton = new Button(container, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		removeButton.setText(Messages.projectUniverseRemoveButtonText);
		removeButton.setToolTipText(Messages.projectUniverseRemoveButtonTooltip);
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				@SuppressWarnings("unchecked")
				List<UniverseFile> selection = ((IStructuredSelection) tableViewer.getSelection()).toList();
				universeDefinitionsModel.removeFiles(selection);
				validate();
			}
		});
	}

	private void validate() {
		StringBuilder error = new StringBuilder();
		error = universeDefinitionsModel.validate(error);
		if (error.length() > 0) {
			setErrorMessage(error.toString());
			setValid(false);
		} else {
			setErrorMessage(null);
			setValid(true);
		}
	}

	@Override
	public boolean performOk() {
		FileLocationStore store = new FileLocationStore(getPreferenceStore(), UNIVERSE_DEFINITION_FILE_LOCATIONS_PREFERENCE);
		store.write(universeDefinitionsModel.getFiles());
		return true;
	}
}
