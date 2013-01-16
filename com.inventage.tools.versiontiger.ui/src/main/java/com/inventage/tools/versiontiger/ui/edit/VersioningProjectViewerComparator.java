package com.inventage.tools.versiontiger.ui.edit;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class VersioningProjectViewerComparator extends ViewerComparator {

	private String sortColumn;
	
	/** The sorting direction, either SWT.UP, or SWT.DOWN. */
	private int sortDirection = SWT.UP;
	
	public VersioningProjectViewerComparator(String comparisonProperty) {
		this.sortColumn = comparisonProperty;
	}
	
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
	
	public void setSortDirection(int sortDirection) {
		this.sortDirection = sortDirection;
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		VersioningProject v1 = (VersioningProject) e1;
		VersioningProject v2 = (VersioningProject) e2;
		
		/* By default, sort by project id. */
		int result = v1.getProjectId().compareTo(v2.getProjectId());
		
		if (sortColumn.equals(VersioningProject.PN_OLD_VERSION)) {
			result = v1.getOldVersion().compareTo(v2.getOldVersion());
		}
		else if (sortColumn.equals(VersioningProject.PN_NEW_VERSION)) {
			
			/* It is possible that there is no new version entry. In that case, we take the old version as secondary criterion. */
			if (v1.getNewVersion() == null || v2.getNewVersion() == null) {
				result = v1.getOldVersion().compareTo(v2.getOldVersion());
			}
			else {
				result = v1.getNewVersion().compareTo(v2.getNewVersion());
			}
		}
		
		if (sortDirection != SWT.UP) {
			return - result;
		}
		
		return result;
	}
}
