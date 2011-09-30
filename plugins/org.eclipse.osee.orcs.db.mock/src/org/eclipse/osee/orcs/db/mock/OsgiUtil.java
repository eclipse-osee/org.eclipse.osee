/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.mock;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class OsgiUtil {

   public static <T> T getService(Class<T> clazz) {
      Bundle bundle = FrameworkUtil.getBundle(OseeDatabase.class);
      ServiceTracker<T, T> tracker = new ServiceTracker<T, T>(bundle.getBundleContext(), clazz, null);
      tracker.open(true);
      T service = tracker.getService();
      tracker.close();
      return service;
   }
}
