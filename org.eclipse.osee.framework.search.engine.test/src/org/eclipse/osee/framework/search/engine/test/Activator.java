package org.eclipse.osee.framework.search.engine.test;

import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchTagger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private static Activator instance;
   private ServiceTracker searchServiceTracker;
   private ServiceTracker taggerServiceTracker;

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      searchServiceTracker = new ServiceTracker(context, ISearchEngine.class.getName(), null);
      searchServiceTracker.open();

      taggerServiceTracker = new ServiceTracker(context, ISearchTagger.class.getName(), null);
      taggerServiceTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      searchServiceTracker.close();
      searchServiceTracker = null;

      taggerServiceTracker.close();
      taggerServiceTracker = null;
      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

   public ISearchEngine getSearchEngine() {
      return (ISearchEngine) searchServiceTracker.getService();
   }

   public ISearchTagger getSearchTagger() {
      return (ISearchTagger) taggerServiceTracker.getService();
   }
}
