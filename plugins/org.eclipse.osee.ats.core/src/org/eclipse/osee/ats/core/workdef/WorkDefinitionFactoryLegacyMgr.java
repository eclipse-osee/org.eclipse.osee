/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
