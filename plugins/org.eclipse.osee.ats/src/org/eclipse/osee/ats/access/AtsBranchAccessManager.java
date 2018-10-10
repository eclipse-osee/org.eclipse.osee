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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.access.AtsBranchAccessContextId;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtilClient;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

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
public class AtsBranchAccessManager implements IArtifactEventListener, EventHandler {

   // Cache to store branch id to context id list so don't have to re-compute
   private static final Map<BranchId, Collection<IAccessContextId>> branchIdToContextIdCache = new HashMap<>(50);

   private final RoleContextProvider roleContextProvider;
   private volatile long cacheUpdated = 0;

   public AtsBranchAccessManager() {
      // Available for osgi instantiation
      this(null);
   }

   public AtsBranchAccessManager(RoleContextProvider roleContextProvider) {
      this.roleContextProvider = roleContextProvider;
   }

   /**
    * True if not common branch and branch's associated artifact is a Team Workflow artifact
    */
   public boolean isApplicable(BranchId objectBranch) {
      boolean result = false;
      try {
         if (AtsClientService.get().getAtsBranch().notEqual(objectBranch)) {
            result = BranchManager.getAssociatedArtifact(objectBranch).isOfType(AtsArtifactTypes.AtsArtifact);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.INFO, "Error determining access applicibility", ex);
      }
      return result;
   }

   public Collection<IAccessContextId> getContextId(BranchId branch) {
      return getContextId(branch, true);
   }

   public Collection<IAccessContextId> getContextId(BranchId branch, boolean useCache) {
      if (useCache && branchIdToContextIdCache.containsKey(branch)) {
         return branchIdToContextIdCache.get(branch);
      }
      Collection<IAccessContextId> contextIds = new ArrayList<>();

      if (branch.isInvalid()) {
         contextIds.add(AtsBranchAccessContextId.DENY_CONTEXT);

         return contextIds;
      }

      branchIdToContextIdCache.put(branch, contextIds);
      try {
         // don't access control common branch artifacts...yet
         if (AtsClientService.get().getAtsBranch().notEqual(branch)) {
            // do this check first since role will supersede others
            if (roleContextProvider != null) {
               contextIds.addAll(roleContextProvider.getContextId(UserManager.getUser()));
            }

            if (contextIds.isEmpty()) {
               // Else, get from associated artifact
               Artifact assocArtifact = BranchManager.getAssociatedArtifact(branch);
               if (assocArtifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  contextIds.addAll(internalGetFromWorkflow((TeamWorkFlowArtifact) assocArtifact));
               } else {
                  contextIds.add(AtsBranchAccessContextId.DENY_CONTEXT);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Exception obtaining Branch Access Context Id; Deny returned", ex);
         contextIds.add(AtsBranchAccessContextId.DENY_CONTEXT);
      }
      return contextIds;
   }

   /**
    * Provided for testing purposes only.
    */
   public Collection<IAccessContextId> internalGetFromWorkflow(IAtsTeamWorkflow teamWf) {
      Set<IAccessContextId> contextIds = new HashSet<>();
      try {
         contextIds.addAll(getFromArtifact(AtsClientService.get().getQueryServiceClient().getArtifact(teamWf)));
         if (contextIds.isEmpty()) {
            for (IAtsActionableItem aia : AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
               teamWf)) {
               Artifact artifact = AtsClientService.get().getQueryServiceClient().getArtifact(aia);
               if (artifact != null) {
                  contextIds.addAll(getFromArtifact(artifact));
               }
               if (!contextIds.isEmpty()) {
                  return contextIds;
               }
            }
            if (contextIds.isEmpty()) {
               Artifact artifact =
                  AtsClientService.get().getQueryServiceClient().getArtifact(teamWf.getTeamDefinition());
               if (artifact != null) {
                  contextIds.addAll(getFromArtifact(artifact));
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Exception obtaining Branch Access Context Id; Deny returned", ex);
         return Arrays.asList(AtsBranchAccessContextId.DENY_CONTEXT);
      }
      return contextIds;
   }

   /**
    * Recursively check artifact and all default hierarchy parents
    */
   private Collection<IAccessContextId> getFromArtifact(Artifact artifact) {
      Set<IAccessContextId> contextIds = new HashSet<>();
      try {
         for (String id : artifact.getAttributesToStringList(CoreAttributeTypes.AccessContextId)) {
            // Do not use getOrCreateId here cause name represents where context ids came from
            // Cache above will take care of this not being created on each access request call.
            contextIds.add(IAccessContextId.valueOf(convertAccessAttributeToContextId(id, artifact),
               "From [" + artifact.getArtifactTypeName() + "]" + artifact.toStringWithId() + " as [" + id + "]"));
         }
         if (contextIds.isEmpty() && artifact.getParent() != null) {
            contextIds.addAll(getFromArtifact(artifact.getParent()));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return contextIds;
   }

   /**
    * ATS "Access Context Id" attribute value can be stored as "id" or "id,name" for easy reading. This method strips
    * ,name out so only id is returned.
    */
   private Long convertAccessAttributeToContextId(String value, Artifact art) {
      String idStr = value.split(",")[0];
      if (Strings.isNumeric(idStr)) {
         return Long.valueOf(idStr);
      } else if (GUID.isValid(idStr)) {
         return roleContextProvider.getContextGuidToIdMap().get(idStr);
      }
      throw new OseeArgumentException("Invalid access value [%s] on artifact %s", value, art.toStringWithId());
   }

   /**
    * Need to process artifact events for Common branch Team Workflows, Actionable Items and Team Definitions in case
    * Access Context Id attribute is edited.
    */
   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return getAtsObjectEventFilters();
   }

   private static final List<IEventFilter> atsObjectEventFilter = new ArrayList<>(2);
   private static final ArtifactTypeEventFilter atsArtifactTypesFilter = new ArtifactTypeEventFilter(
      AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.ActionableItem);

   private synchronized static List<IEventFilter> getAtsObjectEventFilters() {
      try {
         if (atsObjectEventFilter.isEmpty()) {
            atsObjectEventFilter.add(AtsUtilClient.getAtsBranchFilter());
            atsObjectEventFilter.add(atsArtifactTypesFilter);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return atsObjectEventFilter;
   }

   /**
    * Since multiple events of same artifact type can come through, only clear cache every one second
    */
   public synchronized void clearCache() {
      long now = new Date().getTime();
      if (now - cacheUpdated > 1000) {
         branchIdToContextIdCache.clear();
      }
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
         if (guidArt.getArtifactType().matches(AtsArtifactTypes.ActionableItem, AtsArtifactTypes.TeamDefinition)) {
            clearCache();
            return;
         }
         try {
            if (ArtifactTypeManager.getType(guidArt.getArtifactType()).inheritsFrom(AtsArtifactTypes.TeamWorkflow)) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) ArtifactCache.getActive(guidArt);
               if (teamArt != null && teamArt.getWorkingBranch().isValid()) {
                  branchIdToContextIdCache.remove(teamArt.getWorkingBranch());
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void handleEvent(Event event) {
      try {
         clearCache();
      } catch (Exception ex) {
         OseeLog.log(AccessControlManager.class, Level.SEVERE, ex);
      }
   }

   public static void clearCaches() {
      branchIdToContextIdCache.clear();
   }

}
