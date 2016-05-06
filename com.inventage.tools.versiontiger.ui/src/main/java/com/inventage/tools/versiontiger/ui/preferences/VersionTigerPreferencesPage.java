package com.inventage.tools.versiontiger.ui.preferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.inventage.tools.versiontiger.MavenToOsgiVersionMappingStrategy;
import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.VersionRangeChangeStrategy;
import com.inventage.tools.versiontiger.Versioning;
import com.inventage.tools.versiontiger.ui.VersioningUIPlugin;

public class VersionTigerPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	public static String PLUGIN_ID = "VersionTigerPreferencesPage"; //$NON-NLS-1$
	
	private final List<Control> customQualifiersControls = new ArrayList<Control>();
	
	private PreferencesPageModel model;
	private DataBindingContext dataBindingContext = new DataBindingContext();
	

	@Override
	public void init(IWorkbench workbench) {
		model = new PreferencesPageModel(new PreferencesStoreUtil(getPreferenceStore()));
		model.load();

		model.addPropertyChangeListener(PreferencesPageModel.PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateCustomQualifierControlsEnablement();
			}
		});
	}
	
	private void updateCustomQualifierControlsEnablement() {
		boolean enabled = model.getMavenToOsgiVersionMappingStrategy().useCustomQualifiers();
		for (Control control : customQualifiersControls) {
			control.setEnabled(enabled);
		}
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, true));
		
		createOsgiQualifiersGroup(container);

		createVersionRangeChangeStrategy(container);
		
		updateCustomQualifierControlsEnablement();
		
		new CustomPreferencePageSupport(this, dataBindingContext);
		return container;
	}

	private void createOsgiQualifiersGroup(Composite container) {
		Group osgiQualifierGroup = new Group(container, SWT.BORDER);
		osgiQualifierGroup.setText(Messages.preferencesOsgiQualifierTitleLabel);
		osgiQualifierGroup.setLayout(new GridLayout(1, false));
		osgiQualifierGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		createMavenToOsgiVersionMappingStrategy(osgiQualifierGroup);
		
		OsgiQualifierValidator validator = new OsgiQualifierValidator();
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(validator);
		
		createOsgiReleaseQualifier(strategy, osgiQualifierGroup);

		createOsgiSnapshotQualifier(strategy, osgiQualifierGroup);
	}

	private void createMavenToOsgiVersionMappingStrategy(final Group group) {
		ComboViewer mavenToOsgiVersionMapping = new ComboViewer(group);
		mavenToOsgiVersionMapping.setContentProvider(new ArrayContentProvider());
		mavenToOsgiVersionMapping.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MavenToOsgiVersionMappingStrategy) element).getKey();
			}
		});
		mavenToOsgiVersionMapping.setComparator(new ViewerComparator());
		mavenToOsgiVersionMapping.setInput(MavenToOsgiVersionMappingStrategy.values());
		
		IObservableValue versionMappingTargetObservable = ViewerProperties.singleSelection().observe(mavenToOsgiVersionMapping);
		IObservableValue versionMappingModelObservable = BeansObservables.observeValue(model, PreferencesPageModel.PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY);
		dataBindingContext.bindValue(versionMappingTargetObservable, versionMappingModelObservable, null, null);
		
		Label description = new Label(group, SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, true).hint(150, SWT.DEFAULT).applyTo(description);
		
		IObservableValue descriptionTargetObservable = WidgetProperties.text().observe(description);
		IObservableValue descriptionModelObservable = BeansObservables.observeValue(model, PreferencesPageModel.PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY_DESCRIPTION);
		dataBindingContext.bindValue(descriptionTargetObservable, descriptionModelObservable, null, null);
		
		model.addPropertyChangeListener(PreferencesPageModel.PN_MAVEN_TO_OSGI_VERSION_MAPPING_STRATEGY_DESCRIPTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				group.getParent().layout();
			}
		});
	}

	private void createOsgiReleaseQualifier(UpdateValueStrategy strategy, Group osgiQualifierGroup) {
		Label customOsgiReleaseQualifierLabel = new Label(osgiQualifierGroup, SWT.CHECK);
		customOsgiReleaseQualifierLabel.setText(Messages.preferencesOsgiReleaseQualifierLabel);
		GridDataFactory.fillDefaults().indent(0, 20).applyTo(customOsgiReleaseQualifierLabel);
		
		Text customOsgiReleaseQualifier = new Text(osgiQualifierGroup, SWT.BORDER | SWT.FILL);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(customOsgiReleaseQualifier);
		
		IObservableValue releaseText = WidgetProperties.text(SWT.Modify).observe(customOsgiReleaseQualifier);
		IObservableValue releaseModel = BeansObservables.observeValue(model, PreferencesPageModel.PN_OSGI_RELEASE_QUALIFIER);
		dataBindingContext.bindValue(releaseText, releaseModel, strategy, null);
		
		customQualifiersControls.add(customOsgiReleaseQualifierLabel);
		customQualifiersControls.add(customOsgiReleaseQualifier);
	}

	private void createOsgiSnapshotQualifier(UpdateValueStrategy strategy, Group osgiQualifierGroup) {
		Label customOsgiSnapshotQualifierLabel = new Label(osgiQualifierGroup, SWT.CHECK);
		customOsgiSnapshotQualifierLabel.setText(Messages.preferencesOsgiSnapshotQualifierLabel);
		
		Text customOsgiSnapshotQualifier = new Text(osgiQualifierGroup, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(customOsgiSnapshotQualifier);
		
		IObservableValue snapshotText = WidgetProperties.text(SWT.Modify).observe(customOsgiSnapshotQualifier);
		IObservableValue snapshotModel = BeansObservables.observeValue(model, PreferencesPageModel.PN_OSGI_SNAPSHOT_QUALIFIER);
		dataBindingContext.bindValue(snapshotText, snapshotModel, strategy, null);
		
		customQualifiersControls.add(customOsgiSnapshotQualifierLabel);
		customQualifiersControls.add(customOsgiSnapshotQualifier);
	}

	private void createVersionRangeChangeStrategy(final Composite container) {
		Group group = new Group(container, SWT.BORDER);
		group.setText(Messages.VersionTigerPreferencesPage_DependencyGroup);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		
		ComboViewer versionRangeChange = new ComboViewer(group);
		versionRangeChange.setContentProvider(new ArrayContentProvider());
		versionRangeChange.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((VersionRangeChangeStrategy) element).getKey();
			}
		});
		versionRangeChange.setComparator(new ViewerComparator());
		versionRangeChange.setInput(VersionRangeChangeStrategy.values());
		
		IObservableValue versionRangeTargetObservable = ViewerProperties.singleSelection().observe(versionRangeChange);
		IObservableValue versionRangeModelObservable = BeansObservables.observeValue(model, PreferencesPageModel.PN_VERSION_RANGE_CHANGE_STRATEGY);
		dataBindingContext.bindValue(versionRangeTargetObservable, versionRangeModelObservable, null, null);
		
		Label description = new Label(group, SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, true).hint(150, SWT.DEFAULT).applyTo(description);
		
		IObservableValue descriptionTargetObservable = WidgetProperties.text().observe(description);
		IObservableValue descriptionModelObservable = BeansObservables.observeValue(model, PreferencesPageModel.PN_VERSION_RANGE_CHANGE_STRATEGY_DESCRIPTION);
		dataBindingContext.bindValue(descriptionTargetObservable, descriptionModelObservable, null, null);
		
		model.addPropertyChangeListener(PreferencesPageModel.PN_VERSION_RANGE_CHANGE_STRATEGY_DESCRIPTION, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				container.layout();
			}
		});
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return VersioningUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public boolean performOk() {
		save();
		return super.performOk();
	}
	
	@Override
	protected void performApply() {
		save();
		super.performApply();
	}
	
	@Override
	protected void performDefaults() {
		model.setOsgiReleaseQualifier(OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX);
		model.setOsgiSnapshotQualifier(OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX);
		model.setVersionRangeChangeStrategy(VersionRangeChangeStrategy.ADAPTIVE);
		dataBindingContext.updateTargets();
		super.performDefaults();
	}
	
	private void save() {
		model.save();
		
		/* Besides persisting, we set the values of the current Versioning instance. */
		Versioning versioning = VersioningUIPlugin.getDefault().getVersioning();
		versioning.setOsgiReleaseQualifier(model.getOsgiReleaseQualifier());
		versioning.setOsgiSnapshotQualifier(model.getOsgiSnapshotQualifier());
		versioning.setVersionRangeChangeStrategy(model.getVersionRangeChangeStrategy());
	}
}
