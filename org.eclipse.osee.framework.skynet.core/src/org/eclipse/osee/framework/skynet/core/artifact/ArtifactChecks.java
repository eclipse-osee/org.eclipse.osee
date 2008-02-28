/*
 * Created on Feb 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class ArtifactChecks {

   public static List<IArtifactCheck> tasks;
   protected static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactChecks.class);
   public static String EXTENSION_POINT = "org.eclipse.osee.framework.skynet.core.ArtifactCheck";

   public static List<IArtifactCheck> getArtifactChecks() throws Exception {
      if (tasks == null) {
         tasks = new ArrayList<IArtifactCheck>();
         List<IConfigurationElement> iExtensions =
               ExtensionPoints.getExtensionElements(EXTENSION_POINT, "ArtifactCheck");
         for (IConfigurationElement element : iExtensions) {
            String className = element.getAttribute("classname");
            String bundleName = element.getContributor().getName();
            try {
               if (className != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  Class<?> interfaceClass = bundle.loadClass(className);
                  IArtifactCheck check = (IArtifactCheck) interfaceClass.getConstructor().newInstance();
                  tasks.add(check);
               }
            } catch (Exception ex) {
               logger.log(Level.SEVERE, "Problem loading ArtifactCheck extension \"" + className + "\".  Ignorning.",
                     ex);
            }
         }
      }
      return tasks;
   }

}
