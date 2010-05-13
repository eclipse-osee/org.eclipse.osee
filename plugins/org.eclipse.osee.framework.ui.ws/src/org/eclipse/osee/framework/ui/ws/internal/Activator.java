package org.eclipse.osee.framework.ui.ws.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.ws";
   private static Activator plugin;

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      plugin = null;
      super.stop(context);
   }

   /**
    * @return the shared instance
    */
   public static Activator getDefault() {
      return plugin;
   }
}