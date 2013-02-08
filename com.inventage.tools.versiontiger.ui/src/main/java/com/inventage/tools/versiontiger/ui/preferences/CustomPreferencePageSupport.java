package com.inventage.tools.versiontiger.ui.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.databinding.preference.PreferencePageSupport;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferencePage;

/**
 * The {@link PreferencePageSupport} of this JSF version has a bug which causes a NPE. This is why we roll our own one here. Once we update the library, we may switch back. 
 */
public class CustomPreferencePageSupport extends DialogPageSupport {

	protected CustomPreferencePageSupport(DialogPage dialogPage, DataBindingContext dbc) {
		super(dialogPage, dbc);
	}

	@Override
	protected void handleStatusChanged() {
		super.handleStatusChanged();
		((PreferencePage) getDialogPage()).setValid(isNotStale() && hasValidStatus());
	}
	
	private boolean isNotStale() {
		return !currentStatusStale;
	}
	
	private boolean hasValidStatus() {
		if (currentStatus != null) {
			return !currentStatus.matches(IStatus.ERROR | IStatus.CANCEL);
		}
		return true;
	}
}
