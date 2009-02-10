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
package org.eclipse.osee.ote.connection.jini;

import java.util.logging.Level;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.jini.JiniClassServer;
import org.eclipse.osee.framework.jini.discovery.RelaxedSecurity;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.osee.ote.connection.jini";

    // The shared instance
    private static Activator plugin;

    private ServiceTracker connectionServiceTracker;
    private ServiceTracker packageAdminTracker;
    private JiniConnectorRegistrar registrar;

    private ServiceRegistration registration;
    
    private ExportClassLoader exportClassLoader;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
	System.setSecurityManager(new RelaxedSecurity());
	super.start(context);
	plugin = this;



    }

    void startJini() throws Exception {
	try {
	    JiniClassServer.getInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	BundleContext context = getBundle().getBundleContext();
	connectionServiceTracker = new ServiceTracker(context,
		IConnectionService.class.getName(), null);
	connectionServiceTracker.open();

	packageAdminTracker = new ServiceTracker(context, PackageAdmin.class
		.getName(), null);
	packageAdminTracker.open();

	PackageAdmin pa = (PackageAdmin) packageAdminTracker.getService();

	exportClassLoader = new ExportClassLoader(pa);
	IConnectionService service = (IConnectionService) connectionServiceTracker
		.getService();

	registrar = new JiniConnectorRegistrar(exportClassLoader, service);

	// register the service
		registration = context.registerService(IJiniConnectorRegistrar.class.getName(),
		registrar, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
	registrar.shutdown();
	registration.unregister();
	super.stop(context);
	connectionServiceTracker.close();
	packageAdminTracker.close();
	exportClassLoader = null;
	registrar = null;
	plugin = null;
	try {
	    JiniClassServer.stopServer();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
	return plugin;
    }

    ClassLoader getExportClassLoader() {
	return exportClassLoader;
    }

    public static void log(Level level, String message, Throwable t) {
	OseeLog.log(Activator.class, level, message, t);
    }

    public static void log(Level level, String message) {
	log(level, message, null);
    }
}
