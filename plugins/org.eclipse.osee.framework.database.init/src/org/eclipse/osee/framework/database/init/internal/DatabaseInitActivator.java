package org.eclipse.osee.framework.database.init.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class DatabaseInitActivator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.database.init";
   private static BundleContext context;

   public void start(BundleContext context) throws Exception {
      DatabaseInitActivator.context = context;
   }

   public void stop(BundleContext context) throws Exception {
   }

   public static Bundle getBundle() {
      return context.getBundle();
   }
}
