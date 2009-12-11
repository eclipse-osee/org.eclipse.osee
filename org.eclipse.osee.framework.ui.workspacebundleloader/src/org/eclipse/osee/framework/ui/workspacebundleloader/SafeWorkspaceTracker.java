/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.workspace.SafeWorkspaceAccess;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class SafeWorkspaceTracker extends ServiceTracker implements
		IJarChangeListener<WorkspaceStarterNature>, WorkspaceLoader {

	private final Map<String, Bundle> installedBundles;
	private final Map<String, Bundle> runningBundles;
	private final Collection<Bundle> stoppedBundles;
	private JarChangeResourceListener workspaceListener;
	private SafeWorkspaceAccess service;
	private ServiceTracker packageAdminTracker;

	private FileChangeDetector detector = new FileChangeDetector();

	/**
	 * @param context
	 * @param filter
	 * @param customizer
	 */
	public SafeWorkspaceTracker(BundleContext context) {
		super(context, SafeWorkspaceAccess.class.getName(), null);

		packageAdminTracker = new ServiceTracker(context, PackageAdmin.class
				.getName(), null);
		packageAdminTracker.open(true);

		this.installedBundles = new HashMap<String, Bundle>();
		this.runningBundles = new HashMap<String, Bundle>();
		this.stoppedBundles = new LinkedList<Bundle>();

		context.registerService(WorkspaceLoader.class.getName(), this, null);
	}

	@Override
	public Object addingService(ServiceReference reference) {
		service = (SafeWorkspaceAccess) context.getService(reference);
		setupWorkspaceBundleLoadingAfterBenchStartup();
		return super.addingService(reference);
	}

	void setupWorkspaceBundleLoadingAfterBenchStartup() {
		Jobs.runInJob(new PrecompileStartup("Loading Precompiled Libraries",
				Activator.BUNDLE_ID), false);
	}

	private class PrecompileStartup extends AbstractOperation {
		/**
		 * @param operationName
		 * @param pluginId
		 */
		public PrecompileStartup(String operationName, String pluginId) {
			super(operationName, pluginId);
		}

		@Override
		protected void doWork(IProgressMonitor monitor) throws Exception {
			IWorkspace workspace = service.getWorkspace();
			workspaceListener = new JarChangeResourceListener<WorkspaceStarterNature>(
					WorkspaceStarterNature.NATURE_ID, SafeWorkspaceTracker.this);
			try {
				installWorkspacePlugins();
			} catch (CoreException ex) {
				OseeLog.log(Activator.class, Level.SEVERE, ex);
			} catch (BundleException ex) {
				OseeLog.log(Activator.class, Level.SEVERE, ex);
			}
			workspace.addResourceChangeListener(workspaceListener);
		}
	}

	@Override
	public synchronized void close() {
		IWorkspace workspace = service.getWorkspace();
		cleanupHandledBundles();
		workspace.removeResourceChangeListener(workspaceListener);
		super.close();
	}

	/**
    * 
    */
	private void cleanupHandledBundles() {
		for (Bundle bundle : installedBundles.values()) {
			try {
				if (bundle.getState() != Bundle.UNINSTALLED) {
					bundle.uninstall();
				}
			} catch (BundleException ex) {
				OseeLog.log(SafeWorkspaceTracker.class, Level.INFO, ex);
			}
		}
		for (Bundle bundle : runningBundles.values()) {
			try {
				bundle.stop();
				bundle.uninstall();
			} catch (BundleException ex) {
				OseeLog.log(SafeWorkspaceTracker.class, Level.INFO, ex);
			}
		}
		for (Bundle bundle : stoppedBundles) {
			try {
				bundle.uninstall();
			} catch (BundleException ex) {
				OseeLog.log(SafeWorkspaceTracker.class, Level.INFO, ex);
			}
		}
		refreshPackages();
		detector.clear();
		stoppedBundles.clear();
		runningBundles.clear();
		installedBundles.clear();
	}

	/**
	 * @param monitor
	 * @throws CoreException
	 * @throws CoreException
	 * @throws BundleException
	 * @throws BundleException
	 */
	private void installWorkspacePlugins() throws CoreException,
			BundleException {

		loadBundles();
	}

	/**
	 * @param url
	 * @throws BundleException
	 */
	@Override
	public void handleBundleAdded(URL url) {
		try {
			if (detector.isChanged(url)) {
				String urlString = url.toString();
				Bundle bundle = context.installBundle(urlString);
				installedBundles.put(urlString, bundle);
			}
		} catch (BundleException ex) {
			OseeLog.log(SafeWorkspaceTracker.class, Level.SEVERE, ex);
		}
	}

	/**
	 * @param stoppedBundles
	 * @param url
	 * @throws BundleException
	 */
	@Override
	public void handleBundleChanged(URL url) {
		try {
			if (detector.isChanged(url)) {
				String urlString = url.toString();

				// Check to see if this is the first we've seen this
				if (runningBundles.containsKey(urlString)) {
					Bundle bundle = runningBundles.get(urlString);
					System.out.println("\tUpdating plugin "
							+ bundle.getSymbolicName());

					bundle.update();
				} else {
					handleBundleAdded(url);
				}
			}
		} catch (BundleException ex) {
		}
	}

	/**
	 * @param stoppedBundles
	 * @param url
	 * @throws BundleException
	 */
	@Override
	public void handleBundleRemoved(URL url) {
		try {
			detector.remove(url);
			String urlString = url.toString();
			if (runningBundles.containsKey(urlString)) {
				Bundle bundle = runningBundles.get(urlString);
				System.out.println("\tStopping plugin "
						+ bundle.getSymbolicName());

				bundle.stop();
				runningBundles.remove(urlString);
				stoppedBundles.add(bundle);
			}
		} catch (BundleException ex) {
		}
	}

	/**
	 * @throws BundleException
	 */
	private void transitionInstalledPlugins() throws BundleException {
		Iterator<String> iter = installedBundles.keySet().iterator();
		while (iter.hasNext()) {
			String urlString = iter.next();
			Bundle bundle = installedBundles.get(urlString);
			try {
				bundle.start();
				iter.remove();
				runningBundles.put(urlString, bundle);
			} catch (Throwable th) {
				OseeLog.log(Activator.class, Level.SEVERE, th);
			}
		}
		refreshPackages();
	}

	/**
	 * 
	 */
	private void refreshPackages() {
		PackageAdmin packageAdmin = (PackageAdmin) packageAdminTracker
				.getService();
		packageAdmin.refreshPackages(null);
		// try {
		// Thread.sleep(10000);
		// } catch (InterruptedException ex) {
		// }
	}

	/**
	 * @throws BundleException
	 */
	private void transitionStoppedBundles() throws BundleException {
		Iterator<Bundle> iter = stoppedBundles.iterator();
		while (iter.hasNext()) {
			Bundle bundle = iter.next();
			try {
				bundle.uninstall();

				iter.remove();
			} catch (Throwable th) {
				OseeLog.log(Activator.class, Level.SEVERE, th);
			}
		}
		refreshPackages();
	}

	@Override
	public void handlePostChange() {
		try {
			transitionInstalledPlugins();
			transitionStoppedBundles();
		} catch (BundleException ex) {
		}
	}

	@Override
	public void handleNatureClosed(WorkspaceStarterNature nature) {
		closeAllPlugins(nature);
	}

	/**
	 * @param project
	 * @throws CoreException
	 * @throws BundleException
	 */
	private void closeAllPlugins(WorkspaceStarterNature nature) {
		for (URL url : nature.getBundles()) {
			handleBundleRemoved(url);
		}

		try {
			transitionStoppedBundles();
		} catch (BundleException ex) {
		}
	}

	public void loadBundles() throws CoreException, BundleException {
		for (WorkspaceStarterNature starterNature : WorkspaceStarterNature
				.getWorkspaceProjects()) {
			for (URL url : starterNature.getBundles()) {
				try {
					handleBundleAdded(url);
				} catch (Exception ex) {
					OseeLog.log(SafeWorkspaceTracker.class, Level.INFO, ex);
					ex.printStackTrace();
				}
			}
		}
		transitionInstalledPlugins();
	}

	public void unloadBundles() {
		cleanupHandledBundles();
	}
}
