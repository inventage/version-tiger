package com.inventage.tools.versiontiger.ui.preferences;

import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;


public class OsgiQualifierValidator implements IValidator {

	@Override
	public IStatus validate(Object value) {
		String qualifier = (String) value;
		
		/* We would like the qualifier without dot. */
		if (qualifier.startsWith(".")) {
			return ValidationStatus.error("Please omit the prefixing dot.");
		}
		
		/* We don't allow qualifiers containing special characters other than underscores and dashes. */
		Pattern p = Pattern.compile("[^A-Za-z0-9\\-\\_]");
		if (p.matcher(qualifier).find()) {
		    return ValidationStatus.error("Qualifiers may only contain letters, numbers, underscores, and dashes.");
		}
		
		/* Phew, oll klear! */
		return ValidationStatus.ok();
	}

}
