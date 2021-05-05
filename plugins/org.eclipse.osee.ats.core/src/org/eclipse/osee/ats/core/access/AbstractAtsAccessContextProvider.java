/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.access.IAtsAccessContextProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.access.AccessContextResult;
import org.eclipse.osee.framework.core.access.AccessContextResults;
import org.eclipse.osee.framework.core.access.AccessControlUtil;
import org.eclipse.osee.framework.core.access.AccessTypeMatch;
import org.eclipse.osee.framework.core.access.context.AccessContext;
import org.eclipse.osee.framework.core.access.context.AccessType;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsAccessContextProvider implements IAtsAccessContextProvider {

   protected AtsApi atsApi;

   @Override
   public boolean isApplicable(AtsUser atsUser, Object object) {
      boolean result = false;
      if (object instanceof ArtifactToken) {
         result = isApplicable(((ArtifactToken) object).getBranch());
      }
      if (object instanceof BranchId) {
         result = isApplicable((BranchId) object);
      }
      return result;
   }

   /**
    * @return true if this Context Provider is valid fo rhtis db. This should be super fast method as it will be called
    * frequently.
    */
   abstract public boolean isApplicableDb();

   /**
    * Provide additional checks, over associated artifact being Team Workflow, that determines applicability
    */
   public boolean isAtsApplicable(BranchId branch, ArtifactToken assocArt) {
      boolean isApplicableDb = isApplicableDb();
      if (!isApplicableDb) {
         return false;
      }
      boolean applicable = false;
      if (branch.isValid()) {
         BranchType branchType = atsApi.getBranchService().getBranchType(branch);
         if (branchType == BranchType.WORKING) {
            applicable = isAtsApplicable(AtsApiService.get().getBranchService().getParentBranch(branch), assocArt);
         } else if (branchType == BranchType.BASELINE) {
            boolean isAtsBranch = assocArt.notEqual(AtsArtifactToken.AtsCmBranch);
            if (isAtsBranch) {
               applicable = true;
            }
         }
      }
      return applicable;
   }

   /**
    * Default applicability or extend to add addditional checks.
    *
    * @return true if Associate Art is AtsCmBranch or Team Workflow.
    */
   public boolean isApplicable(BranchId branch) {
      boolean applicable = false;
      try {
         if (atsApi.getAtsBranch().notEqual(branch)) {
            ArtifactToken assocArt = null;
            ArtifactId associatedArtifact = atsApi.getBranchService().getAssociatedArtifactId(branch);
            if (associatedArtifact.isValid()) {
               if (associatedArtifact.equals(AtsArtifactToken.AtsCmBranch)) {
                  applicable = true;
               } else {
                  assocArt = atsApi.getQueryService().getArtifact(associatedArtifact);
                  applicable = assocArt.isOfType(AtsArtifactTypes.TeamWorkflow);
               }
            }
            if (applicable) {
               // Force all providers to check extra applicability
               applicable = isAtsApplicable(branch, assocArt);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(getClass(), Level.INFO, "Error determining access applicibility", ex);
      }
      return applicable;
   }

   @Override
   public XResultData hasAttributeTypeContextWriteAccess(AtsUser atsUser, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, XResultData rd) {
      checkContextWrite(artifacts, attributeType, RelationTypeSide.SENTINEL, rd);
      return rd;
   }

   @Override
   public XResultData hasArtifactContextWriteAccess(AtsUser atsUser, Collection<? extends ArtifactToken> artifacts, XResultData rd) {
      checkContextWrite(artifacts, AttributeTypeToken.SENTINEL, RelationTypeSide.SENTINEL, rd);
      return rd;
   }

   @Override
   public XResultData hasRelationContextWriteAccess(AtsUser atsUser, ArtifactToken artifact, RelationTypeToken relationType, XResultData rd) {
      checkContextWrite(Collections.singleton(artifact), AttributeTypeToken.SENTINEL, relationType, rd);
      return rd;
   }

   private XResultData checkContextWrite(Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attrType, RelationTypeToken relType, XResultData rd) {
      Collection<AccessContextToken> contextIds =
         atsApi.getAtsAccessService().getContextIds(artifacts.iterator().next().getBranch());

      for (ArtifactToken artifact : artifacts) {
         AccessContextResults accessResults = new AccessContextResults();
         accessResults.setArtifact(artifact);

         // Store AccessContext types and computed artifact type access and attr type access
         PermissionEnum artPerm = PermissionEnum.READ;
         if (contextIds.isEmpty()) {
            accessResults.setReason(
               "Context Id: No Context Ids found for branch " + artifacts.iterator().next().getBranch().toStringWithId());
            artPerm = PermissionEnum.FULLACCESS;
         } else {
            // Turn contextIds into registered AccessContexts
            Collection<AccessContext> contexts = AccessControlUtil.getContexts(contextIds, rd);
            if (rd.isErrors()) {
               return rd;
            }
            for (AccessContext context : contexts) {
               AccessContextResult aResult = new AccessContextResult();
               accessResults.getContextResults().add(aResult);
               aResult.setContextId(context.getAccessToken());
               aResult.setContext(context);

               // Reverse the order cause base restriction is first; final/override is last
               List<AccessType> accessTypes = new ArrayList<>();
               accessTypes.addAll(context.getTypeAccess());
               Collections.reverse(accessTypes);
               aResult.setAccessTypes(accessTypes);

               for (AccessType accessType : accessTypes) {
                  AccessTypeMatch match =
                     accessType.computeMatch(artifact, attrType, relType, atsApi.getRelationResolver());
                  aResult.setAccessTypeMatch(accessType, match);
                  if (match.isDeny()) {
                     artPerm = PermissionEnum.READ;
                     accessResults.setFinalMatch(match);
                     accessResults.setReason("ContextId: Context " + context.getAccessToken().toStringWithId());
                     break;
                  }
                  if (match.isAllow()) {
                     artPerm = PermissionEnum.WRITE;
                     accessResults.setFinalMatch(match);
                     accessResults.setReason("ContextId: Context " + context.getAccessToken().toStringWithId());
                     break;
                  }
               }
            }
         }

         XResultData results = accessResults.getResults();
         rd.addRaw(results.toString());
         if (!artPerm.matches(PermissionEnum.WRITE)) {
            if (attrType.isValid()) {
               rd.errorf("Context Id: Attr Type [%s] Artifact %s DOES NOT have WRITE access.", attrType,
                  artifact.toStringWithId());
            } else if (relType.isValid()) {
               rd.errorf("Context Id: Rel Type [%s] Artifact %s DOES NOT have WRITE access.", relType,
                  artifact.toStringWithId());
            } else {
               rd.errorf("Context Id: Artifact %s DOES NOT have WRITE access.", artifact.toStringWithId());
            }
         }
      }
      return rd;
   }

}
