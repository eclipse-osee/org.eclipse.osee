/*
 * Created on Apr 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection.impl;

import org.eclipse.osee.framework.db.connection.IDbConnectionInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author b1528444
 */
public class DbConnectionInfoTracker extends ServiceTracker {

   private IDbConnectionInformation service;

   /**
    * @param context
    * @param filter
    * @param customizer
    */
   public DbConnectionInfoTracker(BundleContext context) {
      super(context, IDbConnectionInformation.class.getName(), null);
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
    */
   @Override
   public Object addingService(ServiceReference reference) {
      service = (IDbConnectionInformation) context.getService(reference);
      return service;
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
    */
   @Override
   public void removedService(ServiceReference reference, Object service) {
      context.ungetService(reference);
   }

   public IDbConnectionInformation get() {
      return service;
   }

}
