/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.access;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.access.AtsAccessContextTokens;
import org.eclipse.osee.ats.api.access.IAtsAccessService;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This class will return access context ids related to editing artifacts stored on a team workflow's working branch.
 * <br>
 * <br>
 * Access control can be called frequently, thus a cache is used. Events will clear cache as necessary.<br>
 * <br>
 * Access is determined from "Access Context Id" value stored on Team Workflow, if not there, then Actionable Items, if
 * not there, then Team Defs.
 *
 * @author Donald G. Dunne
 */
public class AtsAccessService implements IAtsAccessService {

   // Cache to store branch id to context id list so don't have to re-compute
   private static final LoadingCache<BranchId, Collection<AccessContextToken>> branchIdToContextIdCache =
      CacheBuilder.newBuilder() //
         .expireAfterWrite(5, TimeUnit.MINUTES).build( //
            new CacheLoader<BranchId, Collection<AccessContextToken>>() {
               @Override
               public Collection<AccessContextToken> load(BranchId branch) {
                  return AtsApiService.get().getAtsAccessService().getContextIds(branch, false);
               }
            });

   private final AtsApi atsApi;
   private Map<String, Long> accessGuidToId = null;

   public AtsAccessService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * True if not common branch and branch's associated artifact is a Team Workflow artifact
    */
   @Override
   public boolean isApplicable(BranchId branch) {
      boolean result = false;
      try {
         if (atsApi.getAtsBranch().notEqual(branch)) {
            ArtifactId associatedArtifact = atsApi.getBranchService().getAssociatedArtifactId(branch);
            if (associatedArtifact.isValid()) {
               ArtifactToken assocArt = atsApi.getQueryService().getArtifact(associatedArtifact);
               result = assocArt.isOfType(AtsArtifactTypes.AtsArtifact);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsAccessService.class, Level.INFO, "Error determining access applicibility", ex);
      }
      return result;
   }

   @Override
   public Collection<AccessContextToken> getContextIds(BranchId branch) {
      return getContextIds(branch, false);
   }

   @Override
   public Collection<AccessContextToken> getContextIds(BranchId branch, boolean useCache) {
      if (useCache) {
         try {
            Collection<AccessContextToken> contextIds = branchIdToContextIdCache.get(branch);
            if (contextIds != null) {
               return contextIds;
            }
         } catch (ExecutionException ex) {
            // do nothing
         }
      }
      Collection<AccessContextToken> contextIds = new ArrayList<>();

      if (branch.isInvalid()) {
         contextIds.add(AtsAccessContextTokens.ATS_BRANCH_READ);
         branchIdToContextIdCache.put(branch, contextIds);
         return contextIds;
      }

      try {
         // don't access control common branch artifacts...yet
         if (atsApi.getAtsBranch().notEqual(branch)) {
            if (contextIds.isEmpty()) {
               // Else, get from associated artifact
               ArtifactId assocArtifact = atsApi.getBranchService().getAssociatedArtifactId(branch);
               ArtifactToken assocArt = atsApi.getQueryService().getArtifact(assocArtifact);
               if (assocArt.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  contextIds.addAll(getFromWorkflow((IAtsTeamWorkflow) assocArt));
               } else {
                  contextIds.add(AtsAccessContextTokens.ATS_BRANCH_READ);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsAccessService.class, Level.SEVERE,
            "Exception obtaining Branch Access Context Id; Deny returned", ex);
         contextIds.add(AtsAccessContextTokens.ATS_BRANCH_READ);
      }
      return contextIds;
   }

   @Override
   public Collection<AccessContextToken> getFromWorkflow(IAtsTeamWorkflow teamWf) {
      Set<AccessContextToken> contextIds = new HashSet<>();
      try {
         contextIds.addAll(getFromArtifact(atsApi.getQueryService().getArtifact(teamWf)));
         if (contextIds.isEmpty()) {
            for (IAtsActionableItem aia : atsApi.getActionableItemService().getActionableItems(teamWf)) {
               ArtifactToken artifact = atsApi.getQueryService().getArtifact(aia);
               if (artifact != null) {
                  contextIds.addAll(getFromArtifact(artifact));
               }
            }
         }
         if (contextIds.isEmpty()) {
            ArtifactToken artifact = atsApi.getQueryService().getArtifact(teamWf.getTeamDefinition());
            if (artifact != null) {
               contextIds.addAll(getFromArtifact(artifact));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsAccessService.class, Level.SEVERE,
            "Exception obtaining Branch Access Context Id; Deny returned", ex);
         return Arrays.asList(AtsAccessContextTokens.ATS_BRANCH_READ);
      }
      return contextIds;
   }

   /**
    * Recursively check artifact and all default hierarchy parents
    */
   private Collection<AccessContextToken> getFromArtifact(ArtifactToken artifact) {
      Set<AccessContextToken> contextIds = new HashSet<>();
      try {
         for (String id : atsApi.getAttributeResolver().getAttributesToStringList(artifact,
            CoreAttributeTypes.AccessContextId)) {
            // Do not use getOrCreateId here cause name represents where context ids came from
            // Cache above will take care of this not being created on each access request call.
            contextIds.add(AccessContextToken.valueOf(convertAccessAttributeToContextId(id, artifact),
               "From [" + artifact.getArtifactType().getName() + "]" + artifact.toStringWithId() + " as [" + id + "]"));
         }
         ArtifactToken parent = atsApi.getRelationResolver().getParent(artifact);
         if (contextIds.isEmpty() && parent != null) {
            contextIds.addAll(getFromArtifact(parent));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsAccessService.class, Level.SEVERE, ex);
      }
      return contextIds;
   }

   /**
    * ATS "Access Context Id" attribute value can be stored as "id" or "id,name" for easy reading. This method strips
    * ,name out so only id is returned.
    */
   private Long convertAccessAttributeToContextId(String value, ArtifactToken art) {
      String idStr = value.split(",")[0];
      if (Strings.isNumeric(idStr)) {
         return Long.valueOf(idStr);
      } else if (GUID.isValid(idStr)) {
         return getContextGuidToIdMap().get(idStr);
      }
      throw new OseeArgumentException("Invalid access value [%s] on artifact %s", value, art.toStringWithId());
   }

   @Override
   public Map<String, Long> getContextGuidToIdMap() {
      if (accessGuidToId == null) {
         accessGuidToId = new ConcurrentHashMap<>();
         ArtifactToken mapArt = atsApi.getQueryService().getArtifact(CoreArtifactTokens.AccessIdMap);
         if (mapArt.isValid()) {
            String mapStr =
               atsApi.getAttributeResolver().getSoleAttributeValue(mapArt, CoreAttributeTypes.GeneralStringData, "");
            for (String line : mapStr.split("\n")) {
               String[] values = line.split(",");
               String guid = values[1];
               Long id = Long.valueOf(values[0]);
               accessGuidToId.put(guid, id);
            }
         }
      }
      return accessGuidToId;
   }

   @Override
   public void setContextIds(IAtsObject atsObject, AccessContextToken... contextIds) {
      IAtsChangeSet changes = atsApi.createChangeSet("Set Context Ids");
      for (AccessContextToken token : contextIds) {
         changes.addAttribute(atsObject, CoreAttributeTypes.AccessContextId,
            String.format("%s, %s", token.getIdString(), token.getName()));
      }
      changes.execute();
   }

   @Override
   public void clearCaches() {
      branchIdToContextIdCache.invalidateAll();
   }

   @Override
   public boolean isWorkflowEditable(IAtsWorkItem workItem) {
      boolean isAccessControlWrite = AtsApiService.get().getStoreService().isAccessControlWrite(workItem);
      if (isAccessControlWrite) {
         return true;
      }
      return false;
   }

}
