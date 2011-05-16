/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

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
         ExtensionDefinedObjects<IWorkDefintionFactoryLegacyMgr> objects =
            new ExtensionDefinedObjects<IWorkDefintionFactoryLegacyMgr>(
               "org.eclipse.osee.ats.core.AtsLegacyWorkDefinitionProvider", "AtsLegacyWorkDefinitionProvider",
               "classname");
         legacyMgr = objects.getObjects().iterator().next();
      }
   }

}
