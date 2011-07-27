/*
 * Created on Sep 17, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.ats.core.config;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public abstract class ProgramManagers {

   public static IAtsProgramManager getAtsProgramManager(TeamWorkFlowArtifact teamArt) {
      for (IAtsProgramManager program : getAtsProgramManagers()) {
         if (program.isApplicable(teamArt)) {
            return program;
         }
      }
      return null;
   }

   @SuppressWarnings("rawtypes")
   public static Set<IAtsProgramManager> getAtsProgramManagers() {
      Set<IAtsProgramManager> lbaProgramItems = new HashSet<IAtsProgramManager>();
      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.core.AtsProgramManager");
      if (point == null) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't access AtsProgram extension point");
         return null;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsProgramManager")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     lbaProgramItems.add((IAtsProgramManager) obj);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, "Error loading AtsProgramManager extension", ex);
                  }
               }
            }
         }
      }
      return lbaProgramItems;
   }
}