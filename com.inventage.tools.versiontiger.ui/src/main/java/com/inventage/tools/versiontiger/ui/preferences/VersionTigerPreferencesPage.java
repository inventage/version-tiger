package com.inventage.tools.versiontiger.ui.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
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

import com.inventage.tools.versiontiger.OsgiVersion;
import com.inventage.tools.versiontiger.Versioning;
import com.inventage.tools.versiontiger.ui.VersioningUIPlugin;

public class VersionTigerPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	public static String PLUGIN_ID = "VersionTigerPreferencesPage";
	
	private OsgiQualifiersModel osgiQualifiersModel;
	private DataBindingContext dataBindingContext = new DataBindingContext();
	

	@Override
	public void init(IWorkbench workbench) {
		osgiQualifiersModel = new OsgiQualifiersModel(new OsgiQualifiersStore(getPreferenceStore()));
		osgiQualifiersModel.load();
	}
	
	@Override
	protected Control createContents(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		
		
		OsgiQualifierValidator validator = new OsgiQualifierValidator();
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(validator);
		
		/* OSGI qualifiers */
		Group osgiQualifierGroup = new Group(container, SWT.BORDER);
		osgiQualifierGroup.setText(Messages.preferencesOsgiQualifierTitleLabel);
		osgiQualifierGroup.setLayout(new GridLayout(1, false));
		osgiQualifierGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		/* Release qualifier setting */
		Label customOsgiReleaseQualifierLabel = new Label(osgiQualifierGroup, SWT.CHECK);
		customOsgiReleaseQualifierLabel.setText(Messages.preferencesOsgiReleaseQualifierLabel);
		
		final Text customOsgiReleaseQualifier = new Text(osgiQualifierGroup, SWT.BORDER | SWT.FILL);
		customOsgiReleaseQualifier.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		/* Add data binding for field. */
		IObservableValue releaseText = WidgetProperties.text(SWT.Modify).observe(customOsgiReleaseQualifier);
		IObservableValue releaseModel = BeansObservables.observeValue(osgiQualifiersModel, OsgiQualifiersModel.PN_OSGI_RELEASE_QUALIFIER);
		dataBindingContext.bindValue(releaseText, releaseModel, strategy, null);

		/* Snapshot qualifier setting */
		Label customOsgiSnapshotQualifierLabel = new Label(osgiQualifierGroup, SWT.CHECK);
		customOsgiSnapshotQualifierLabel.setText(Messages.preferencesOsgiSnapshotQualifierLabel);
		
		final Text customOsgiSnapshotQualifier = new Text(osgiQualifierGroup, SWT.BORDER);
		customOsgiSnapshotQualifier.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		/* Add data binding for field. */
		IObservableValue snapshotText = WidgetProperties.text(SWT.Modify).observe(customOsgiSnapshotQualifier);
		IObservableValue snapshotModel = BeansObservables.observeValue(osgiQualifiersModel, OsgiQualifiersModel.PN_OSGI_SNAPSHOT_QUALIFIER);
		dataBindingContext.bindValue(snapshotText, snapshotModel, strategy, null);
		
		// Place for possible other settings.
		// 
		// ...
		
		new CustomPreferencePageSupport(this, dataBindingContext);
		return container;
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
		osgiQualifiersModel.setOsgiReleaseQualifier(OsgiVersion.OSGI_DEFAULT_RELEASE_SUFFIX);
		osgiQualifiersModel.setOsgiSnapshotQualifier(OsgiVersion.OSGI_DEFAULT_SNAPSHOT_SUFFIX);
		dataBindingContext.updateTargets();
		super.performDefaults();
	}
	
	private void save() {
		osgiQualifiersModel.save();
		
		/* Besides persisting, we set the values of the current Versioning instance. */
		Versioning versioning = VersioningUIPlugin.getDefault().getVersioning();
		versioning.setOsgiReleaseQualifier(osgiQualifiersModel.getOsgiReleaseQualifier());
		versioning.setOsgiSnapshotQualifier(osgiQualifiersModel.getOsgiSnapshotQualifier());
	}
}
