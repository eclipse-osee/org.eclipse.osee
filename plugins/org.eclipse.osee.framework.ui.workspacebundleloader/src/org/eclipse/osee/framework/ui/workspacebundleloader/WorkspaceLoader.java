/*
 * Created on Dec 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.workspacebundleloader;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.BundleException;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface WorkspaceLoader {
	public void loadBundles() throws CoreException, BundleException;
	public void unloadBundles();
}
