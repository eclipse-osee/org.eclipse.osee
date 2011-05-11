/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionFactoryLegacyMgr {

   public static WorkDefinitionMatch getWorkFlowDefinitionFromId(String id) throws OseeCoreException {
      ensureLoaded();
      return legacyMgr.getWorkFlowDefinitionFromId(id);
   }

   public static WorkDefinitionMatch getWorkFlowDefinitionFromReverseId(String id) throws OseeCoreException {
      ensureLoaded();
      return legacyMgr.getWorkFlowDefinitionFromReverseId(id);
   }

   public static WorkDefinitionMatch getWorkFlowDefinitionFromArtifact(Artifact artifact) throws OseeCoreException {
      ensureLoaded();
      return legacyMgr.getWorkFlowDefinitionFromArtifact(artifact);
   }

   public static WorkDefinitionMatch getWorkFlowDefinitionFromTeamDefinition(TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      ensureLoaded();
      return legacyMgr.getWorkFlowDefinitionFromTeamDefition(teamDefinition);
   }

   public static String getOverrideId(String legacyId) {
      ensureLoaded();
      return legacyMgr.getOverrideId(legacyId);
   }

   private static IWorkDefintionFactoryLegacyMgr legacyMgr;

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   public synchronized static void ensureLoaded() {
      if (legacyMgr == null) {

         IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint(
               "org.eclipse.osee.ats.core.AtsLegacyWorkDefinitionProvider");
         if (point == null) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
               "Can't access AtsLegacyWorkDefinitionProvider extension point");
         }
         IExtension[] extensions = point.getExtensions();
         for (IExtension extension : extensions) {
            IConfigurationElement[] elements = extension.getConfigurationElements();
            String classname = null;
            String bundleName = null;
            for (IConfigurationElement el : elements) {
               if (el.getName().equals("AtsLegacyWorkDefinitionProvider")) {
                  classname = el.getAttribute("classname");
                  bundleName = el.getContributor().getName();
                  if (classname != null && bundleName != null) {
                     Bundle bundle = Platform.getBundle(bundleName);
                     try {
                        Class<?> taskClass = bundle.loadClass(classname);
                        Object obj = taskClass.newInstance();
                        legacyMgr = (IWorkDefintionFactoryLegacyMgr) obj;
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
                           "Error loading AtsLegacyWorkDefinitionProvider extension", ex);
                     }
                  }
               }
            }
         }
      }
   }

}
