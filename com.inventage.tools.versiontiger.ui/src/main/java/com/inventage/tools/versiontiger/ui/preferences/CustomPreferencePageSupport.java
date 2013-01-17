package com.inventage.tools.versiontiger.ui.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.PreferencePage;

public class CustomPreferencePageSupport extends DialogPageSupport {

	protected CustomPreferencePageSupport(DialogPage dialogPage, DataBindingContext dbc) {
		super(dialogPage, dbc);
	}

	@Override
	protected void handleStatusChanged() {
		super.handleStatusChanged();
		
		if (currentStatus != null) {
			((PreferencePage) getDialogPage()).setValid(!currentStatus.matches(IStatus.ERROR | IStatus.CANCEL));
		}
	}
}
