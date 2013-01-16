package com.inventage.tools.versiontiger.universedefinition.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author nw
 */
public abstract class AbstractPropertyChangeSupport {

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}
}
