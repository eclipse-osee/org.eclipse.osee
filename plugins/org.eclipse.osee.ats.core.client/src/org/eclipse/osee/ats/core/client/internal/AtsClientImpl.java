/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsEventService;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.agile.AgileService;
import org.eclipse.osee.ats.core.ai.ActionableItemManager;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.core.client.branch.internal.AtsBranchServiceImpl;
import org.eclipse.osee.ats.core.client.internal.config.ActionableItemFactory;
import org.eclipse.osee.ats.core.client.internal.config.TeamDefinitionFactory;
import org.eclipse.osee.ats.core.client.internal.config.VersionFactory;
import org.eclipse.osee.ats.core.client.internal.ev.AtsEarnedValueImpl;
import org.eclipse.osee.ats.core.client.internal.query.AtsQueryServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workdef.ArtifactResolverImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsRelationResolverServiceImpl;
import org.eclipse.osee.ats.core.client.task.AtsTaskService;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.util.IArtifactMembersCache;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionListeners;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsCoreServiceImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public class AtsClientImpl extends AtsCoreServiceImpl implements IAtsClient {

   private IActionableItemFactory actionableItemFactory;
   private ITeamDefinitionFactory teamDefFactory;

   private ArtifactCollectorsCache<GoalArtifact> goalMembersCache;
   private ArtifactCollectorsCache<SprintArtifact> sprintItemsCache;
   private IAtsEventService eventService;
   private IAgileService agileService;

   public AtsClientImpl() {
      super();
   }

   @Override
   public void start() throws OseeCoreException {
      attributeResolverService = new AtsAttributeResolverServiceImpl();

      super.start();

      earnedValueService = new AtsEarnedValueImpl(logger, getServices());

      configItemFactory = new ConfigItemFactory(logger, this);

      actionableItemFactory = new ActionableItemFactory();
      teamDefFactory = new TeamDefinitionFactory();
      workItemFactory = new WorkItemFactory(this);
      versionFactory = new VersionFactory();

      artifactResolver = new ArtifactResolverImpl(this);
      relationResolver = new AtsRelationResolverServiceImpl(this);

      branchService = new AtsBranchServiceImpl(this, teamWorkflowProvidersLazy);

      logFactory = AtsCoreFactory.newLogFactory();
      stateFactory = AtsCoreFactory.newStateFactory(getServices(), logFactory);
      storeService = new AtsStoreService(workItemFactory, getUserServiceClient(), jdbcService);

      queryService = new AtsQueryServiceImpl(this, jdbcService);
      actionableItemManager = new ActionableItemManager(attributeResolverService, storeService, this);

      actionFactory = new ActionFactory(workItemFactory, getSequenceProvider(), actionableItemManager,
         attributeResolverService, stateFactory, getServices());
      taskService = new AtsTaskService(this);

      eventService = new AtsEventServiceImpl();
      agileService = new AgileService(logger, this);

   }

   @Override
   public void stop() {
      super.stop();

      versionService = null;
      actionableItemFactory = null;
      teamDefFactory = null;
   }

   public void setAttributeResolverService(IAttributeResolver attributeResolverService) {
      this.attributeResolverService = attributeResolverService;
   }

   @Override
   public Artifact getConfigArtifact(IAtsConfigObject atsConfigObject) throws OseeCoreException {
      return (Artifact) atsConfigObject.getStoreObject();
   }

   @Override
   public List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects) throws OseeCoreException {
      return Collections.castAll(AtsObjects.getArtifacts(atsObjects));
   }

   @Override
   public void reloadWorkDefinitionCache(boolean pend) throws OseeCoreException {
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            workDefCache.invalidate();
            XResultData resultData = new XResultData();
            try {
               for (IAtsWorkDefinition workDef : workDefService.getAllWorkDefinitions(resultData)) {
                  workDefCache.cache(workDef.getId(), workDef);
               }
            } catch (Exception ex) {
               // do nothing
            }
         }
      };
      if (pend) {
         reload.run();
      } else {
         new Thread(reload).start();
      }
   }

   @Override
   public void reloadUserCache(boolean pend) throws OseeCoreException {
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            configProvider.clearConfigurationsCaches();
            getUserService().reloadCache();
         }
      };
      if (pend) {
         reload.run();
      } else {
         new Thread(reload).start();
      }
   }

   @Override
   public void reloadAllCaches(boolean pend) throws OseeCoreException {
      reloadUserCache(pend);
      reloadWorkDefinitionCache(pend);
      reloadConfigCache(pend);
   }

   @Override
   public void reloadConfigCache(boolean pend) {
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            try {
               List<Integer> ids = new LinkedList<>();
               for (Long id : configProvider.getConfigurations().getAtsConfigIds()) {
                  ids.add(id.intValue());
               }
               List<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(ids, getAtsBranch());
               for (Artifact artifact : artifacts) {
                  IAtsConfigObject configObj = configItemFactory.getConfigObject(artifact);
                  if (configObj != null) {
                     atsCache.cacheAtsObject(configObj);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      };
      if (pend) {
         reload.run();
      } else {
         new Thread(reload).start();
      }
   }

   @Override
   public void invalidateAllCaches() {
      super.invalidateAllCaches();
      invalidateWorkDefinitionCache();
      if (goalMembersCache != null) {
         goalMembersCache.invalidate();
      }
      if (sprintItemsCache != null) {
         sprintItemsCache.invalidate();
      }
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      return createTeamDefinition(GUID.create(), name, AtsUtilClient.createConfigObjectUuid(), changes, services);
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String guid, String name, long uuid, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      IAtsTeamDefinition item = teamDefFactory.createTeamDefinition(guid, name, uuid, changes, services);
      atsCache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      return createActionableItem(GUID.create(), name, AtsUtilClient.createConfigObjectUuid(), changes, services);
   }

   @Override
   public IAtsActionableItem createActionableItem(String guid, String name, long uuid, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      IAtsActionableItem item = actionableItemFactory.createActionableItem(guid, name, uuid, changes, services);
      atsCache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsVersionService getVersionService() throws OseeStateException {
      return versionService;
   }

   @Override
   public IAtsUserServiceClient getUserServiceClient() throws OseeStateException {
      return (IAtsUserServiceClient) userService;
   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(ArtifactId artifact) throws OseeCoreException {
      if (artifact instanceof Artifact) {
         return (Artifact) artifact;
      }
      return ArtifactQuery.getArtifactFromId(artifact, getAtsBranch());
   }

   @Override
   public Artifact getArtifact(IAtsObject atsObject) throws OseeCoreException {
      Artifact results = null;
      if (atsObject.getStoreObject() != null) {
         results = (Artifact) atsObject.getStoreObject();
      }
      return results;
   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(Long uuid) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(uuid <= 0, "Uuid must be > 0; is %d", uuid);
      Artifact result = null;
      try {
         result = ArtifactQuery.getArtifactFromId(uuid.intValue(), getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public IAtsWorkItemService getWorkItemService() throws OseeStateException {
      return workItemService;
   }

   @Override
   public AbstractWorkflowArtifact getWorkflowArtifact(IAtsObject atsObject) throws OseeCoreException {
      return (AbstractWorkflowArtifact) getArtifact(atsObject);
   }

   @Override
   public void sendNotifications(final AtsNotificationCollector notifications) {
      if (AtsUtilClient.isEmailEnabled()) {
         Jobs.startJob(new Job("Send Notifications") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               AtsClientService.getNotifyEndpoint().sendNotifications(notifications);
               return Status.OK_STATUS;
            }
         }, false);
      }
   }

   @Override
   public boolean isNotificationsEnabled() {
      return AtsUtilClient.isEmailEnabled();
   }

   @Override
   public void setNotifactionsEnabled(boolean enabled) {
      AtsUtilClient.setEmailEnabled(enabled);
   }

   @Override
   public String getConfigValue(String key) {
      String result = null;
      Artifact atsConfig = ArtifactQuery.getArtifactFromToken(AtsArtifactToken.AtsConfig, getAtsBranch());
      if (atsConfig != null) {
         for (Object obj : atsConfig.getAttributeValues(CoreAttributeTypes.GeneralStringData)) {
            String str = (String) obj;
            if (str.startsWith(key)) {
               result = str.replaceFirst(key + "=", "");
               break;
            }
         }
      }
      return result;
   }

   @Override
   public IAtsServices getServices() {
      return this;
   }

   @Override
   public ChangeType getChangeType(IAtsAction fromAction) {
      return ChangeTypeUtil.getChangeType(fromAction);
   }

   @Override
   public Collection<IArtifactType> getArtifactTypes() {
      List<IArtifactType> types = new ArrayList<>();
      types.addAll(ArtifactTypeManager.getAllTypes());
      return types;
   }

   @Override
   public void setChangeType(IAtsObject atsObject, ChangeType changeType, IAtsChangeSet changes) {
      ChangeTypeUtil.setChangeType(atsObject, changeType);
   }

   @Override
   public IAtsProgramService getProgramService() {
      return programService;
   }

   @Override
   public Artifact getArtifactById(String id) {
      Artifact result = null;
      if (GUID.isValid(id)) {
         result = getArtifactByGuid(id);
      }
      if (result == null && Strings.isNumeric(id)) {
         result = getArtifact(Long.valueOf(id));
      }
      if (result == null) {
         result = getArtifactByAtsId(id);
      }
      return result;
   }

   @Override
   public Artifact getArtifactByAtsId(String id) {
      return (Artifact) super.getArtifactByAtsId(id);
   }

   @Override
   public Artifact getArtifactByGuid(String guid) throws OseeCoreException {
      return ArtifactQuery.getArtifactFromId(guid, getAtsBranch());
   }

   @Override
   public IArtifactMembersCache<GoalArtifact> getGoalMembersCache() {
      if (goalMembersCache == null) {
         goalMembersCache = new ArtifactCollectorsCache<>(AtsRelationTypes.Goal_Member);
      }
      return goalMembersCache;
   }

   @Override
   public IArtifactMembersCache<SprintArtifact> getSprintItemsCache() {
      if (sprintItemsCache == null) {
         sprintItemsCache = new ArtifactCollectorsCache<>(AtsRelationTypes.AgileSprintToItem_AtsItem);
      }
      return sprintItemsCache;
   }

   @Override
   public Artifact checkArtifactFromId(long uuid, BranchId atsBranch) {
      return ArtifactQuery.checkArtifactFromId((int) uuid, getAtsBranch(), DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz) throws OseeCoreException {
      return getCache().getAtsObject(uuid);
   }

   @Override
   public Collection<ITransitionListener> getTransitionListeners() {
      return TransitionListeners.getListeners();
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      ((AbstractWorkflowArtifact) workItem).clearImplementersCache();
   }

   @Override
   public ArtifactToken getArtifactByName(IArtifactType artType, String name) {
      return ArtifactQuery.getArtifactFromTypeAndNameNoException(artType, name, getAtsBranch());
   }

   @Override
   public IAtsEventService getEventService() {
      return eventService;
   }

   @Override
   public IAtsChangeSet createChangeSet(String comment) {
      return getStoreService().createAtsChangeSet(comment, getUserService().getCurrentUser());
   }

   @Override
   public OseeClient getOseeClient() {
      return OsgiUtil.getService(getClass(), OseeClient.class);
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(List<Long> ids) {
      List<Integer> intIds = new LinkedList<>();
      for (Long id : ids) {
         intIds.add(id.intValue());
      }
      return Collections.castAll(
         ArtifactQuery.getArtifactListFromIds(intIds, getAtsBranch(), DeletionFlag.EXCLUDE_DELETED));
   }

   @Override
   public IAgileService getAgileService() {
      return agileService;
   }

}
