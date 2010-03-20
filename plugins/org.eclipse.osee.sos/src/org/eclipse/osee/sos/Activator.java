package org.eclipse.osee.sos;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
   private BundleContext myContext;
   private static Activator instance;

   public void start(BundleContext context) throws Exception {
      myContext = context;
      instance = this;
      myContext.registerService(CommandProvider.class.getName(), new SosCommand(), null);
   }

   public void stop(BundleContext context) throws Exception {
      instance = null;
   }

   public BundleContext getContext() {
      return myContext;
   }

   public static Activator getInstance() {
      return Activator.instance;
   }
}
