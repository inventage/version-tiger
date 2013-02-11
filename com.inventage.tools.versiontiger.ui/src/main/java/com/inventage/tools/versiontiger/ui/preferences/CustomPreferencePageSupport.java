package com.inventage.tools.versiontiger.ui.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.preference.PreferencePage;

/**
 * Replacement class for {@code PreferencePageSupport} which is buggy in Galileo
 * (preferencePage is not fetched with getDialogPage(): causes a NPE).
 */
public class CustomPreferencePageSupport extends DialogPageSupport {

	public CustomPreferencePageSupport(PreferencePage preferencePage, DataBindingContext dbc) {
		super(preferencePage, dbc);
	}

	@Override
	protected void handleStatusChanged() {
		super.handleStatusChanged();
		
		getDialogPage().setValid(isNotStale() && hasValidStatus());
	}

	@Override
	protected PreferencePage getDialogPage() {
		return (PreferencePage) super.getDialogPage();
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
