/*
 * Created on Apr 23, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author b1528444
 */
public class BindTracker extends ServiceTracker {

   private IBind binder;

   /**
    * @param context
    * @param filter
    * @param customizer
    */
   public BindTracker(BundleContext context, String clazz, IBind binder) {
      super(context, clazz, null);
      this.binder = binder;
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
    */
   @Override
   public Object addingService(ServiceReference reference) {
      Object obj = context.getService(reference);
      binder.bind(obj);
      return obj;
   }

   /* (non-Javadoc)
    * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
    */
   @Override
   public void removedService(ServiceReference reference, Object service) {
      binder.unbind(service);
      context.ungetService(reference);
   }
}
