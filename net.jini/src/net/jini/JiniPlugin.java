package net.jini;

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class to be used in the desktop.
 */
public class JiniPlugin implements BundleActivator {

	private static JiniPlugin plugin;
	private String[] serviceGroups;
	private BundleContext context;

	public static JiniPlugin getInstance() {
		return plugin;
	}

	public String[] getJiniVersion() {
		Bundle bundle = context.getBundle();
		try {

			if (bundle != null) {
				URL home = bundle.getEntry("/");
				System.out.println(home.toString());
				String id = home.getFile();
				System.out.println(id);
				if (id.endsWith("/")) {
					id = id.substring(0, id.length() - 1);
					System.out.println(id);
				}
				id = id.substring(id.lastIndexOf("/") + 1, id.length());
				System.out.println(id);
				serviceGroups = new String[1];
				serviceGroups[0] = id;
			}
		} catch (Exception e) {
			System.err.println("Failed to extract jini version");
			e.printStackTrace();
		}
		return serviceGroups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		this.context = context;
		serviceGroups = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext arg0) throws Exception {
		plugin = null;
		this.context = null;
	}
}
