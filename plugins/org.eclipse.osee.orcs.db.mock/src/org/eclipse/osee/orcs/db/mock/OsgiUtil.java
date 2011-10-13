/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.db.mock;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class OsgiUtil {

   public static <T> T getService(Class<T> clazz) throws OseeCoreException {
      Bundle bundle = FrameworkUtil.getBundle(OsgiUtil.class);
      Assert.assertNotNull(bundle);

      int bundleState = bundle.getState();
      if (bundleState != Bundle.STARTING && bundleState != Bundle.ACTIVE) {
         try {
            bundle.start();
         } catch (BundleException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      BundleContext context = bundle.getBundleContext();
      Assert.assertNotNull(context);

      ServiceTracker<T, T> tracker = new ServiceTracker<T, T>(context, clazz, null);
      tracker.open(true);
      T service = tracker.getService();
      tracker.close();

      Assert.assertNotNull(service);
      return service;
   }
}
