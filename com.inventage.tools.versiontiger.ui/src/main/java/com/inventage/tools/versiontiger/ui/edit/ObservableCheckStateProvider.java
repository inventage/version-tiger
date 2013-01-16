package com.inventage.tools.versiontiger.ui.edit;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.StructuredViewer;

public class ObservableCheckStateProvider<T extends StructuredViewer & ICheckable>  implements ICheckStateProvider{

	private final T checkableViewer;
	private final IValueProperty checkedAttribute;
	private final IObservableMap checkedMap;
	private final IValueProperty grayedAttribute;
	private final CheckStateListener checkStateListener;
	private final RefreshViewerListener refreshViewerListener;

	public ObservableCheckStateProvider(T checkableViewer, IObservableSet checkableElements, IValueProperty checkedAttribute) {
		this(checkableViewer, checkableElements, checkedAttribute, null);
	}

	public ObservableCheckStateProvider(T checkableViewer, IObservableSet checkableElements, IValueProperty checkedAttribute, IValueProperty grayedAttribute) {
		this.checkableViewer = checkableViewer;
		this.checkedAttribute = checkedAttribute;
		this.checkedMap = checkedAttribute.observeDetail(checkableElements);
		this.grayedAttribute = grayedAttribute;
		checkStateListener = new CheckStateListener(checkedMap);
		refreshViewerListener = new RefreshViewerListener(checkableViewer);
		addCheckStateListener();
		addRefreshViewerListener();
	}
	
	private void addCheckStateListener() {
		checkableViewer.addCheckStateListener(checkStateListener);
	}
	
	private void removeCheckStateListener() {
		checkableViewer.removeCheckStateListener(checkStateListener);
	}
	
	private void addRefreshViewerListener() {
		checkedMap.addChangeListener(refreshViewerListener);
	}
	
	private void removeRefreshViewerListener() {
		checkedMap.removeChangeListener(refreshViewerListener);
	}

	@Override
	public boolean isChecked(Object element) {
		return (Boolean) checkedAttribute.observe(element).getValue();
	}

	@Override
	public boolean isGrayed(Object element) {
		return grayedAttribute != null && (Boolean) grayedAttribute.observe(element).getValue();
	}
	
	public void dispose() {
		removeCheckStateListener();
		removeRefreshViewerListener();
	}
	
	private static class RefreshViewerListener implements IChangeListener {
		private final StructuredViewer viewer;

		public RefreshViewerListener(StructuredViewer viewer) {
			this.viewer = viewer;
		}
		
		@Override
		public void handleChange(ChangeEvent event) {
			viewer.refresh();
		}
	}
	
	private static class CheckStateListener implements ICheckStateListener {
		
		private IObservableMap checkedMap;

		public CheckStateListener(IObservableMap checkedMap) {
			this.checkedMap = checkedMap;
		}
		
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			checkedMap.put(event.getElement(), event.getChecked());
		}
	}
}
