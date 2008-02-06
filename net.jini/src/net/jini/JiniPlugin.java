package net.jini;

import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

/**
 * The main plug-in class to be used in the desktop.
 */
public class JiniPlugin extends Plugin {

   private static JiniPlugin plugin;
   private String[] serviceGroups;

   public JiniPlugin() {
      plugin = this;
      serviceGroups = null;
   }

   public static JiniPlugin getInstance() {
      return plugin;
   }

   public String[] getJiniVersion() {
      //      if (serviceGroups == null) {
      Bundle bundle = Platform.getBundle("net.jini");
      try {
         if (bundle != null) {
            URL home = FileLocator.resolve(bundle.getEntry("/"));
            String id = home.getFile();
            if (id.endsWith("/")) {
               id = id.substring(0, id.length() - 1);
            }
            id = id.substring(id.lastIndexOf("/") + 1, id.length());
            serviceGroups = new String[1];
            serviceGroups[0] = id;
         }
      } catch (Exception e) {
         System.err.println("Failed to extract jini version");
         e.printStackTrace();
      }
      //      }
      return serviceGroups;
   }
}
