package org.eclipse.osee.ats.dsl.integration.internal;

import org.eclipse.osee.ats.dsl.integration.AtsModelingServiceRegHandler;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author Donald G. Dunne
 */
public class Activator implements BundleActivator {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core.dsl.integration";

   private ServiceDependencyTracker dependencyTracker1;

   @Override
   public void start(BundleContext context) throws Exception {
      dependencyTracker1 = new ServiceDependencyTracker(context, new AtsModelingServiceRegHandler());
      dependencyTracker1.open();

   }

   @Override
   public void stop(BundleContext context) throws Exception {
      Lib.close(dependencyTracker1);
   }
}
