package com.inventage.tools.versiontiger.ui.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Sets;
import com.inventage.tools.versiontiger.ProjectUniverse;
import com.inventage.tools.versiontiger.strategy.IncrementBugfixAndSnapshotStrategy;
import com.inventage.tools.versiontiger.strategy.IncrementMajorAndSnapshotStrategy;
import com.inventage.tools.versiontiger.strategy.IncrementMinorAndSnapshotStrategy;
import com.inventage.tools.versiontiger.strategy.ReleaseStrategy;
import com.inventage.tools.versiontiger.strategy.SetVersionManuallyStrategy;
import com.inventage.tools.versiontiger.strategy.VersioningStrategy;
import com.inventage.tools.versiontiger.ui.VersioningUIPlugin;
import com.inventage.tools.versiontiger.ui.edit.mavenrootdialog.AskForRootFolderOnUpdateValueStrategy;
import com.inventage.tools.versiontiger.universedefinition.UniverseDefinitions;

public class EditVersionPage extends WizardPage {
	private static final String EDIT_VERSION_SELECTED_UNIVERSE_PREFERENCE = "editVersionSelectedUniverse"; //$NON-NLS-1$

	private final EditVersionModel editVersionModel;
	private final DataBindingContext dbc;

	private ComboViewer universeDefinitionCombo;
	private CheckboxTableViewer projectPreviewTable;
	
	/* The available versioning strategies, manually added below. To include an new versioning strategy, add an implementation of VersioningStrategy and
	 * add an instance to the list below.*/
	private List<VersioningStrategy> strategies = new ArrayList<VersioningStrategy>();

	EditVersionPage(EditVersionModel versionModel) {
		super(Messages.editVersionWizardPageTitle);

		setTitle(Messages.editVersionWizardPageTitle);
		setDescription(Messages.editVersionWizardPageDescription);

		this.editVersionModel = versionModel;
		this.dbc = new DataBindingContext();

		strategies.add(new ReleaseStrategy());
		strategies.add(new IncrementMajorAndSnapshotStrategy());
		strategies.add(new IncrementMinorAndSnapshotStrategy());
		strategies.add(new IncrementBugfixAndSnapshotStrategy());
		strategies.add(new SetVersionManuallyStrategy());
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = initializeLayoutContainer(parent);

		createUniverseSection(container);
		createVersionSection(container);

		setControl(container);
		
		/* Since on load there is no strategy selected, we don't allow finishing now. */
		setPageComplete(false);
	}

	private Composite initializeLayoutContainer(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		result.setLayout(new GridLayout());
		return result;
	}

	private void createUniverseSection(Composite parent) {

		/* Create group. */
		final Group universeGroup = new Group(parent, SWT.NONE);
		universeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		universeGroup.setLayout(new GridLayout(1, false));
		universeGroup.setText(Messages.editVersionWizardPageUniverseGroupText);

		/* Insert combo in own composite. */
		final Composite comboComposite = new Composite(universeGroup, SWT.NONE);
		comboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comboComposite.setLayout(new GridLayout(2, false));

		universeDefinitionCombo = createUniverseDefinitionCombo(comboComposite);
		bindUniverseDefinition(universeDefinitionCombo);

		/* Insert table viewer. */
		projectPreviewTable = createProjectPreviewTable(universeGroup);
		bindProjectPreviewTable();

		Composite buttonContainer = new Composite(universeGroup, SWT.NONE);
		buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonContainer.setLayout(new GridLayout(2, false));

		Button selectAllButton = new Button(buttonContainer, SWT.PUSH);
		selectAllButton.setText("Select all");
		selectAllButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				editVersionModel.setAllSelected(true);
			}
		});

		Button selectNoneButton = new Button(buttonContainer, SWT.PUSH);
		selectNoneButton.setText("Select none");
		selectNoneButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				editVersionModel.setAllSelected(false);
			}
		});
	}

	private ComboViewer createUniverseDefinitionCombo(Composite parent) {
		Label universeDefinitionComboLabel = new Label(parent, SWT.NONE);
		universeDefinitionComboLabel.setText(Messages.editVersionWizardPageUniverseDefinitionLabel);
		universeDefinitionComboLabel.setLayoutData(new GridData());

		ComboViewer result = new ComboViewer(parent, SWT.READ_ONLY);
		result.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		result.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (!selection.isEmpty()) {
					ProjectUniverse projectUniverse = (ProjectUniverse) selection.getFirstElement();
					String projectId = projectUniverse.id();
					VersioningUIPlugin.getDefault().getPreferenceStore().setValue(EDIT_VERSION_SELECTED_UNIVERSE_PREFERENCE, projectId);
				}
			}
		});
		return result;
	}

	private void bindUniverseDefinition(ComboViewer universeDefinitionCombo) {
		universeDefinitionCombo.setContentProvider(new ArrayContentProvider());
		universeDefinitionCombo.setLabelProvider(new ProjectUniverseLabelProvider());
		Set<ProjectUniverse> projectUniverses = getProjectUniverses();
		universeDefinitionCombo.setInput(projectUniverses);

		IObservableValue selectedUniverseDefinition = ViewerProperties.singleSelection().observe(universeDefinitionCombo);
		IObservableValue universeDefinition = BeansObservables.observeValue(editVersionModel, EditVersionModel.PN_PROJECT_UNIVERSE);
		dbc.bindValue(selectedUniverseDefinition, universeDefinition, new AskForRootFolderOnUpdateValueStrategy(getShell()), null);

		ProjectUniverse projectUniverse = findUniverse(projectUniverses, getUniverseId());
		if (projectUniverse == null) {
			projectUniverse = findUniverse(projectUniverses, UniverseDefinitions.ALL_WORKSPACE_PROJECTS_UNIVERSE_ID);
		}
		/* We simply use the first element in the list as the initial selection. */
		if (0 < universeDefinitionCombo.getCombo().getItemCount()) {
			universeDefinitionCombo.setSelection(new StructuredSelection(projectUniverse));
		}
		editVersionModel.setProjectSelection(getProjectIds(projectUniverse));
	}

	private String getUniverseId() {
		String universeId = VersioningUIPlugin.getDefault().getPreferenceStore().getString(EDIT_VERSION_SELECTED_UNIVERSE_PREFERENCE);
		if (universeId == null) {
			universeId = UniverseDefinitions.ALL_WORKSPACE_PROJECTS_UNIVERSE_ID;
		}
		return universeId;
	}

	private ProjectUniverse findUniverse(Set<ProjectUniverse> projectUniverses, String universeId) {
		for (ProjectUniverse projectUniverse : projectUniverses) {
			if (projectUniverse.id().equals(universeId)) {
				return projectUniverse;
			}
		}
		return null;
	}

	private Set<String> getProjectIds(ProjectUniverse projectUniverse) {
		EditVersionWizard wizard = (EditVersionWizard) getWizard();
		Set<String> projectIds = Sets.newHashSet();
		if (projectUniverse != null) {
			for (IProject selectedProject : wizard.getSelectedProjects()) {
				String projectId = projectUniverse.idForProjectPath(selectedProject.getLocation().toString());
				if (projectId != null) {
					projectIds.add(projectId);
				}
			}
		}
		return projectIds;
	}

	private Set<ProjectUniverse> getProjectUniverses() {
		return VersioningUIPlugin.getDefault().getUniverseDefinitions().getProjectUniverses(editVersionModel.getLogger());
	}

	private CheckboxTableViewer createProjectPreviewTable(Composite parent) {

		/* With the checkbox table viewer, the first column automatically contains a checkbox. */
		CheckboxTableViewer tableViewer = new CheckboxTableViewer(new TableViewer(parent, SWT.CHECK).getTable());
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		/* Create columns. */
		TableViewerColumn projectId = createTableViewerColumn(tableViewer, 200, Messages.editVersionWizardPageColumnHeaderProjectName, VersioningProject.PN_PROJECT_ID);
		createTableViewerColumn(tableViewer, 140, Messages.editVersionWizardPageColumnHeaderCurrentVersion, VersioningProject.PN_OLD_VERSION);
		createTableViewerColumn(tableViewer, 140, Messages.editVersionWizardPageColumnHeaderNewVersion, VersioningProject.PN_NEW_VERSION);

		/* Set the default comparator which sorts by the first column - the project id. */
		tableViewer.setComparator(new VersioningProjectViewerComparator(VersioningProject.PN_PROJECT_ID));
		
		/* To let the table display the sort indicator we need to set the sort column and direction explicitly. */
		tableViewer.getTable().setSortColumn(projectId.getColumn());
		tableViewer.getTable().setSortDirection(SWT.UP);
		
		
		return tableViewer;
	}
	
	private TableViewerColumn createTableViewerColumn(final CheckboxTableViewer tableViewer, int columnWidth, String columnTitle, final String propertyName) {
		
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setWidth(columnWidth);
		tableViewerColumn.getColumn().setText(columnTitle);

		tableViewerColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				TableColumn tableColumn = (TableColumn) e.getSource();
				Table table = tableViewer.getTable();
				VersioningProjectViewerComparator versioningProjectViewerComparator = (VersioningProjectViewerComparator) tableViewer.getComparator();
				
				int newSortDirection = SWT.UP;
				
				/* We only need to change the sort direction to 'down' if we're on the same column already and the sort direction is 'up'. */
				if (table.getSortColumn() != null && table.getSortColumn().equals(tableColumn) && table.getSortDirection() == SWT.UP) {
					newSortDirection = SWT.DOWN;
				}
				
				table.setSortDirection(newSortDirection);
				versioningProjectViewerComparator.setSortDirection(newSortDirection);
				
				table.setSortColumn(tableColumn);
				versioningProjectViewerComparator.setSortColumn(propertyName);
				tableViewer.refresh();
			}
		});
		
		return tableViewerColumn;
	}

	private void bindProjectPreviewTable() {
		IObservableSet observableVersioningProjects = BeansObservables.observeSet(editVersionModel, EditVersionModel.PN_PROJECTS);
		ViewerSupport.bind(projectPreviewTable, observableVersioningProjects,
				BeanProperties.values(new String[] { VersioningProject.PN_PROJECT_ID, VersioningProject.PN_OLD_VERSION, VersioningProject.PN_NEW_VERSION }));
		projectPreviewTable.setCheckStateProvider(new ObservableCheckStateProvider<CheckboxTableViewer>(projectPreviewTable, observableVersioningProjects,
				BeanProperties.value(VersioningProject.PN_SELECTED)));
	}

	private void createVersionSection(Composite parent) {

		/* Group definition. */
		Group versionGroup = new Group(parent, SWT.NONE);
		versionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		versionGroup.setLayout(new GridLayout(3, false));
		versionGroup.setText(Messages.editVersionWizardPageVersionGroupText);
		
		final Image warningImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);

		/*
		 * We create a radio button for every strategy defined above. If the
		 * strategy asks for additional data, we render the also painted text
		 * box visible.
		 */
		for (final VersioningStrategy versioningStrategy : strategies) {

			final Button b = new Button(versionGroup, SWT.RADIO);
			final Text additionalDataField = new Text(versionGroup, SWT.BORDER);

			final Label wrongVersionFormatWarningLabel = new Label(versionGroup, SWT.ICON);
			wrongVersionFormatWarningLabel.setImage(warningImage);
			wrongVersionFormatWarningLabel.setToolTipText(Messages.editVersionWizardPageWarningToolTipWrongVersionFormat);
			wrongVersionFormatWarningLabel.setVisible(false);

			additionalDataField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			additionalDataField.setVisible(versioningStrategy.requiresDataInput());
			additionalDataField.addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent e) {

					/*
					 * If we fail to set the version strategy, because the data
					 * is invalid, we show a warning icon.
					 */
					try {
						editVersionModel.setVersionStrategy(editVersionModel.getVersioningStrategy().setData(additionalDataField.getText()));
						wrongVersionFormatWarningLabel.setVisible(false);
						setPageComplete(true);
					} catch (IllegalArgumentException e2) {
						wrongVersionFormatWarningLabel.setVisible(true);
						setPageComplete(false);
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {
				}
			});

			b.setData(versioningStrategy);
			b.setText(versioningStrategy.toString());
			b.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (versioningStrategy.requiresDataInput()) {

						/*
						 * If we fail to set the version strategy, because the
						 * data is invalid, we show a warning icon.
						 */
						try {
							editVersionModel.setVersionStrategy(versioningStrategy.setData(additionalDataField.getText()));
							wrongVersionFormatWarningLabel.setVisible(false);
							setPageComplete(true);

						} catch (IllegalArgumentException e2) {
							wrongVersionFormatWarningLabel.setVisible(true);
							setPageComplete(false);
						}
					} else {
						editVersionModel.setVersionStrategy(versioningStrategy);
						setPageComplete(true);
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
		}
	}

	private final class ProjectUniverseLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			ProjectUniverse universe = getProjectUniverse(element);
			return universe == null ? super.getText(element) : universe.name();
		}

		private ProjectUniverse getProjectUniverse(Object element) {
			if (element instanceof ProjectUniverse) {
				return (ProjectUniverse) element;
			}
			return null;
		}
	}

}
