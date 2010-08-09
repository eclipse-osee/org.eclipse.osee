/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.access;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.actions.wizard.IAtsTeamWorkflow;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkflowExtensions;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * This class will return access context ids for artifacts stored on a team workflow's related branch
 */
public class AtsBranchObjectManager {

   public static AtsBranchObjectContextId getContextId(Artifact artifact) {
      try {
         // If artifact has a context id on it, use that
         AtsBranchObjectContextId id = getFromArtifact(artifact);
         if (id != null) {
            return id;
         }
         // Else, get branch associated artifact to determine permissions
         Artifact branchAssocArt =
            ArtifactQuery.getArtifactFromId(artifact.getBranch().getAssociatedArtifactId(),
               BranchManager.getCommonBranch());
         if (branchAssocArt != null) {
            if (branchAssocArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
               id = getFromWorkflow((TeamWorkFlowArtifact) branchAssocArt);
               if (id != null) {
                  return id;
               }
            } else if (branchAssocArt instanceof IATSArtifact) {
               return AtsBranchObjectContextId.DENY_CONTEXT;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Exception obtaining Branch Access Context Id; Deny returned", ex);
         return AtsBranchObjectContextId.DENY_CONTEXT;
      }
      return AtsBranchObjectContextId.DEFAULT_BRANCH_CONTEXT;
   }

   public static AtsBranchObjectContextId getFromWorkflow(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      for (IAtsTeamWorkflow teamWorkflowExt : TeamWorkflowExtensions.getInstance().getAtsTeamWorkflowExtensions()) {
         AtsBranchObjectContextId id = teamWorkflowExt.getBranchAccessContextIdFromWorkflow(teamArt);
         if (id != null) {
            return id;
         }
      }
      for (ActionableItemArtifact aia : teamArt.getActionableItemsDam().getActionableItems()) {
         AtsBranchObjectContextId id = getFromArtifact(aia);
         if (id != null) {
            return id;
         }
      }
      AtsBranchObjectContextId id = getFromArtifact(teamArt.getTeamDefinition());
      if (id != null) {
         return id;
      }
      return null;
   }

   public static AtsBranchObjectContextId getFromArtifact(Artifact artifact) {
      if (artifact.isOfType(CoreArtifactTypes.AbstractAccessControlled)) {
         try {
            List<String> attributes = artifact.getAttributesToStringList(CoreAttributeTypes.AccessContextId);
            if (!attributes.isEmpty()) {
               if (attributes.size() > 1) {
                  OseeLog.log(
                     AtsPlugin.class,
                     Level.SEVERE,
                     String.format("Unexpected multiple context ids [%s] for artifact [%s][%s]", attributes,
                        artifact.getHumanReadableId(), artifact));

               }
               return AtsBranchObjectContextId.get(attributes.iterator().next());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }
}
