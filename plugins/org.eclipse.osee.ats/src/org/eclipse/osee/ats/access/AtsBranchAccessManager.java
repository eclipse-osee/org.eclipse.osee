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
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.access.AtsBranchAccessContextId;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.OseeEventService;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEvent;
import org.eclipse.osee.framework.skynet.core.event.model.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * This class will return access context ids related to editing artifacts stored on a team workflow's working branch.<br>
 * <br>
 * Access control can be called frequently, thus a cache is used. Events will clear cache as necessary.<br>
 * <br>
 * Access is determined from "Access Context Id" value stored on Team Workflow, if not there, then Actionable Items, if
 * not there, then Team Defs.
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchAccessManager implements IArtifactEventListener, IAccessControlEventListener {

   // Cache to store artifact guid to context id list so don't have to re-compute
   private final Map<String, Collection<IAccessContextId>> branchGuidToContextIdCache =
      new HashMap<String, Collection<IAccessContextId>>(50);

   private static final List<Long> atsConfigArtifactTypes = Arrays.asList(AtsArtifactTypes.ActionableItem.getGuid(),
      AtsArtifactTypes.TeamDefinition.getGuid());

   private final RoleContextProvider roleContextProvider;
   private volatile long cacheUpdated = 0;

   private final OseeEventService eventService;

   public AtsBranchAccessManager(OseeEventService eventService) {
      this(eventService, null);

   }

   public AtsBranchAccessManager(OseeEventService eventService, RoleContextProvider roleContextProvider) {
      this.eventService = eventService;
      this.roleContextProvider = roleContextProvider;
      eventService.addListener(EventQosType.NORMAL, this);
   }

   public void dispose() {
      eventService.removeListener(EventQosType.NORMAL, this);
   }

   private Artifact getAssociatedArtifact(IOseeBranch branch) throws OseeCoreException {
      Artifact toReturn = null;
      int artId = BranchManager.getBranch(branch).getAssociatedArtifactId();
      if (artId > 0) {
         toReturn = ArtifactQuery.getArtifactFromId(artId, AtsUtil.getAtsBranchToken());
      } else {
         toReturn = UserManager.getUser(SystemUser.OseeSystem);
      }
      return toReturn;
   }

   /**
    * True if not common branch and branch's associated artifact is a Team Workflow artifact
    */
   public boolean isApplicable(IOseeBranch objectBranch) {
      boolean result = false;
      try {
         if (!AtsUtil.getAtsBranchToken().equals(objectBranch)) {
            ArtifactType assocArtType = getAssociatedArtifact(objectBranch).getArtifactType();
            if (assocArtType != null) {
               result = assocArtType.inheritsFrom(AtsArtifactTypes.AtsArtifact);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.INFO, "Error determining access applicibility", ex);
      }
      return result;
   }

   public Collection<IAccessContextId> getContextId(IOseeBranch branch) {
      if (branchGuidToContextIdCache.containsKey(branch.getGuid())) {
         return branchGuidToContextIdCache.get(branch.getGuid());
      }

      Collection<IAccessContextId> contextIds = new ArrayList<IAccessContextId>();
      branchGuidToContextIdCache.put(branch.getGuid(), contextIds);
      try {
         // don't access control common branch artifacts...yet
         if (!AtsUtil.getAtsBranchToken().equals(branch)) {
            // do this check first since role will supersede others
            if (roleContextProvider != null) {
               contextIds.addAll(roleContextProvider.getContextId(UserManager.getUser()));
            }

            if (contextIds.isEmpty()) {
               // Else, get from associated artifact
               Artifact assocArtifact = getAssociatedArtifact(branch);
               ArtifactType assocArtType = assocArtifact.getArtifactType();
               if (assocArtType.inheritsFrom(AtsArtifactTypes.TeamWorkflow)) {
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
   public static Collection<IAccessContextId> internalGetFromWorkflow(TeamWorkFlowArtifact teamArt) {
      Set<IAccessContextId> contextIds = new HashSet<IAccessContextId>();
      try {
         contextIds.addAll(getFromArtifact(teamArt));
         if (contextIds.isEmpty()) {
            for (IAtsActionableItem aia : teamArt.getActionableItemsDam().getActionableItems()) {
               Artifact artifact = AtsClientService.get().getConfigArtifact(aia);
               if (artifact != null) {
                  contextIds.addAll(getFromArtifact(artifact));
               }
               if (!contextIds.isEmpty()) {
                  return contextIds;
               }
            }
            if (contextIds.isEmpty()) {
               Artifact artifact = AtsClientService.get().getConfigArtifact(teamArt.getTeamDefinition());
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
   private static Collection<IAccessContextId> getFromArtifact(Artifact artifact) {
      Set<IAccessContextId> contextIds = new HashSet<IAccessContextId>();
      try {
         for (String guid : artifact.getAttributesToStringList(CoreAttributeTypes.AccessContextId)) {
            // Do not use getOrCreateId here cause name represents where context ids came from
            // Cache above will take care of this not being created on each access request call.
            contextIds.add(TokenFactory.createAccessContextId(convertAccessAttributeToGuid(guid),
               "From [" + artifact.getArtifactTypeName() + "]" + artifact.toStringWithId() + " as [" + guid + "]"));
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
    * ATS "Access Context Id" attribute value can be stored as "guid" or "guid,name" for easy reading. This method
    * strips ,name out so only guid is returned.
    */
   private static String convertAccessAttributeToGuid(String value) {
      return value.split(",")[0];
   }

   /**
    * Need to process artifact events for Common branch Team Workflows, Actionable Items and Team Definitions in case
    * Access Context Id attribute is edited.
    */
   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return getAtsObjectEventFilters();
   }

   private static final List<IEventFilter> atsObjectEventFilter = new ArrayList<IEventFilter>(2);
   private static final ArtifactTypeEventFilter atsArtifactTypesFilter = new ArtifactTypeEventFilter(
      AtsArtifactTypes.TeamWorkflow, AtsArtifactTypes.TeamDefinition, AtsArtifactTypes.ActionableItem);

   private synchronized static List<IEventFilter> getAtsObjectEventFilters() {
      try {
         if (atsObjectEventFilter.isEmpty()) {
            atsObjectEventFilter.add(EventUtil.getCommonBranchFilter());
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
   private synchronized void clearCache() {
      long now = new Date().getTime();
      if (now - cacheUpdated > 1000) {
         branchGuidToContextIdCache.clear();
      }
   }

   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEvent accessControlEvent) {
      if (accessControlEvent.getEventType() == AccessControlEventType.BranchAccessControlModified) {
         clearCache();
      }
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
         if (atsConfigArtifactTypes.contains(guidArt.getArtTypeGuid())) {
            clearCache();
            return;
         }
         try {
            if (ArtifactTypeManager.getType(guidArt).inheritsFrom(AtsArtifactTypes.TeamWorkflow)) {
               TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) ArtifactCache.getActive(guidArt);
               if (teamArt != null && teamArt.getWorkingBranch() != null) {
                  branchGuidToContextIdCache.remove(teamArt.getWorkingBranch().getGuid());
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

}
