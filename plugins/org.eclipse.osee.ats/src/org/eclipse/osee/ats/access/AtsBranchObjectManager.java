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

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * This class will return access context ids for artifacts stored on a team workflow's related branch
 */
public class AtsBranchObjectManager implements AtsAccessContextIdResolver {

   private final Collection<IAtsAccessControlService> atsAccessServices;

   private final IOseeBranch atsBranch;

   public AtsBranchObjectManager(IOseeBranch atsBranch, Collection<IAtsAccessControlService> atsAccessServices) {
      this.atsBranch = atsBranch;
      this.atsAccessServices = atsAccessServices;
   }

   private ArtifactType getAssociatedArtifactType(Branch objectBranch) throws OseeCoreException {
      Artifact assocArtifact = ArtifactQuery.getArtifactFromId(objectBranch.getAssociatedArtifactId(), atsBranch);
      return assocArtifact.getArtifactType();
   }

   public boolean isApplicable(Branch objectBranch) throws OseeCoreException {
      boolean result = false;
      if (!atsBranch.equals(objectBranch)) {
         ArtifactType assocArtType = getAssociatedArtifactType(objectBranch);
         if (assocArtType != null) {
            result = assocArtType.inheritsFrom(AtsArtifactTypes.TeamWorkflow);
         }
      }
      return result;
   }

   public AccessContextId getContextId(Artifact artifact) {
      AccessContextId id = null;
      try {
         // If artifact has a context id on it, use that
         id = getFromArtifact(artifact);
         if (id == null) {
            Artifact assocArtifact =
               ArtifactQuery.getArtifactFromId(artifact.getBranch().getAssociatedArtifactId(), atsBranch);
            ArtifactType assocArtType = assocArtifact.getArtifactType();
            if (assocArtType.inheritsFrom(AtsArtifactTypes.TeamWorkflow)) {
               id = getFromWorkflow((TeamWorkFlowArtifact) assocArtifact);
            } else if (assocArtifact instanceof IATSArtifact) {
               id = AtsBranchObjectContextId.DENY_CONTEXT;
            } else {
               id = AtsBranchObjectContextId.DEFAULT_BRANCH_CONTEXT;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Exception obtaining Branch Access Context Id; Deny returned", ex);
         id = AtsBranchObjectContextId.DENY_CONTEXT;
      }
      return id;
   }

   private AccessContextId getFromWorkflow(TeamWorkFlowArtifact teamArt) {
      try {
         if (atsAccessServices != null) {
            for (IAtsAccessControlService service : atsAccessServices) {
               IAtsAccessControlService accessService = service;
               AccessContextId id = accessService.getBranchAccessContextIdFromWorkflow(teamArt);
               if (id != null) {
                  return id;
               }
            }
         }
         for (ActionableItemArtifact aia : teamArt.getActionableItemsDam().getActionableItems()) {
            AccessContextId id = getFromArtifact(aia);
            if (id != null) {
               return id;
            }
         }
         AccessContextId id = getFromArtifact(teamArt.getTeamDefinition());
         if (id != null) {
            return id;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Exception obtaining Branch Access Context Id; Deny returned", ex);
         return AtsBranchObjectContextId.DENY_CONTEXT;
      }
      return null;
   }

   private AccessContextId getFromArtifact(Artifact artifact) {
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
               String guid = attributes.iterator().next();
               return AtsAccessContextIdFactory.getOrCreate(guid);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }
}
