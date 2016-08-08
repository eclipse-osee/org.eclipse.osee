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
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.config.IAtsConfigurationProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsEventService;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsActionFactory;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.ai.ActionableItemManager;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.core.client.branch.internal.AtsBranchServiceImpl;
import org.eclipse.osee.ats.core.client.config.IAtsClientVersionService;
import org.eclipse.osee.ats.core.client.internal.config.ActionableItemFactory;
import org.eclipse.osee.ats.core.client.internal.config.TeamDefinitionFactory;
import org.eclipse.osee.ats.core.client.internal.config.VersionFactory;
import org.eclipse.osee.ats.core.client.internal.ev.AtsEarnedValueImpl;
import org.eclipse.osee.ats.core.client.internal.query.AtsQueryServiceImpl;
import org.eclipse.osee.ats.core.client.internal.review.AtsReviewServiceImpl;
import org.eclipse.osee.ats.core.client.internal.store.AtsVersionServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workdef.ArtifactResolverImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsRelationResolverServiceImpl;
import org.eclipse.osee.ats.core.client.task.AtsTaskService;
import org.eclipse.osee.ats.core.client.team.AtsTeamDefinitionService;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.util.IArtifactMembersCache;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionListeners;
import org.eclipse.osee.ats.core.config.AtsCache;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.program.AtsProgramService;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsCoreServiceImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionAdminImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionCache;
import org.eclipse.osee.ats.core.workflow.AtsImplementersService;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
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
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class AtsClientImpl extends AtsCoreServiceImpl implements IAtsClient {

   private IAtsStateFactory stateFactory;
   private IAtsWorkDefinitionService workDefService;
   private IArtifactResolver artifactResolver;
   private IAtsClientVersionService versionService;
   private IAtsCache atsCache;
   private IActionableItemFactory actionableItemFactory;
   private ITeamDefinitionFactory teamDefFactory;
   private IVersionFactory versionFactory;
   private AtsWorkDefinitionCache workDefCache;
   private IAtsEarnedValueService earnedValueService;
   private IAtsUserServiceClient userServiceClient;
   private IAtsWorkItemService workItemService;
   private IAtsBranchService branchService;
   private IAtsReviewService reviewService;
   private IAttributeResolver attributeResolverService;
   private ISequenceProvider sequenceProvider;
   private IAtsActionFactory actionFactory;
   private IAtsLogFactory atsLogFactory;
   private IAtsStateFactory atsStateFactory;
   private IAtsWorkStateFactory workStateFactory;
   private IAtsLogFactory logFactory;
   private IAtsColumnService columnServices;
   private IAtsUtilService utilService;
   private JdbcService jdbcService;
   private IAtsWorkItemFactory workItemFactory;
   private IAtsConfigItemFactory configItemFactory;
   private IAtsActionableItemService actionableItemManager;
   private IRelationResolver relationResolver;
   private IAtsProgramService programService;
   private IAtsTeamDefinitionService teamDefinitionService;
   private IAtsQueryService atsQueryService;
   private ArtifactCollectorsCache<GoalArtifact> goalMembersCache;
   private ArtifactCollectorsCache<SprintArtifact> sprintItemsCache;
   private AtsStoreService atsStoreService;
   private TeamWorkflowProviders teamWorkflowProvidersLazy;
   private IAtsTaskService taskService;
   private Log logger;
   List<IAtsSearchDataProvider> searchDataProviders;
   private IAtsEventService eventService;
   private IAtsImplementerService implementerService;
   private IAtsConfigurationProvider configProvider;

   public AtsClientImpl() {
      searchDataProviders = new ArrayList<>();
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setAtsWorkDefinitionService(IAtsWorkDefinitionService workDefService) {
      this.workDefService = workDefService;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setConfigurationsService(IAtsConfigurationProvider configProvider) {
      this.configProvider = configProvider;
   }

   public void setAtsUserService(IAtsUserServiceClient userServiceClient) {
      this.userServiceClient = userServiceClient;
   }

   public void addSearchDataProvider(IAtsSearchDataProvider provider) {
      searchDataProviders.add(provider);
   }

   public void removeSearchDataProvider(IAtsSearchDataProvider provider) {
      searchDataProviders.remove(provider);
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(workDefService, "IAtsWorkDefinitionService");

      atsCache = new AtsCache(this);
      earnedValueService = new AtsEarnedValueImpl(logger, getServices());

      configItemFactory = new ConfigItemFactory(logger, this);
      versionService = new AtsVersionServiceImpl(this, atsCache);

      actionableItemFactory = new ActionableItemFactory();
      teamDefFactory = new TeamDefinitionFactory();
      workItemFactory = new WorkItemFactory(this);
      versionFactory = new VersionFactory();

      workDefCache = new AtsWorkDefinitionCache();

      artifactResolver = new ArtifactResolverImpl(this);
      teamWorkflowProvidersLazy = new TeamWorkflowProviders();
      workItemService = new AtsWorkItemServiceImpl(this, teamWorkflowProvidersLazy);
      attributeResolverService = new AtsAttributeResolverServiceImpl();
      relationResolver = new AtsRelationResolverServiceImpl(this);

      workDefService.setWorkDefinitionStringProvider(this);

      workDefAdmin = new AtsWorkDefinitionAdminImpl(workDefCache, workDefService, attributeResolverService,
         teamWorkflowProvidersLazy);
      branchService = new AtsBranchServiceImpl(this, teamWorkflowProvidersLazy);
      reviewService = new AtsReviewServiceImpl(this);

      atsLogFactory = AtsCoreFactory.newLogFactory();
      atsStateFactory = AtsCoreFactory.newStateFactory(getServices(), atsLogFactory);
      atsStoreService = new AtsStoreService(workItemFactory);

      atsQueryService = new AtsQueryServiceImpl(this, jdbcService);
      actionableItemManager = new ActionableItemManager(attributeResolverService, atsStoreService, this);
      sequenceProvider = new ISequenceProvider() {

         @Override
         public long getNext(String sequenceName) {
            // Sequence is set to sequential
            return jdbcService.getClient().getNextSequence(sequenceName, false);
         }
      };
      utilService = AtsCoreFactory.getUtilService(attributeResolverService);

      programService = new AtsProgramService(this);
      teamDefinitionService = new AtsTeamDefinitionService(this);

      actionFactory = new ActionFactory(workItemFactory, utilService, sequenceProvider, actionableItemManager,
         attributeResolverService, atsStateFactory, getServices());
      taskService = new AtsTaskService(this);

      eventService = new AtsEventServiceImpl();

   }

   public void setAttributeResolverService(IAttributeResolver attributeResolverService) {
      this.attributeResolverService = attributeResolverService;
   }

   @Override
   public IVersionFactory getVersionFactory() {
      return versionFactory;
   }

   public void stop() {
      if (workDefAdmin != null) {
         workDefAdmin.clearCaches();
      }
      workDefAdmin = null;
      atsCache = null;
      workDefCache = null;
      versionService = null;
      actionableItemFactory = null;
      teamDefFactory = null;
      versionFactory = null;

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
   public void invalidateCache() {
      atsCache.invalidate();
   }

   @Override
   public void reloadWorkDefinitionCache(boolean pend) throws OseeCoreException {
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            workDefCache.invalidate();
            workDefCache.getAllWorkDefinitions();
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
               List<Artifact> artifacts = ArtifactQuery.getArtifactListFromIds(ids, AtsUtilCore.getAtsBranch());
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
      invalidateCache();
      invalidateWorkDefinitionCache();
      if (goalMembersCache != null) {
         goalMembersCache.invalidate();
      }
      if (sprintItemsCache != null) {
         sprintItemsCache.invalidate();
      }
   }

   @Override
   public void invalidateWorkDefinitionCache() {
      workDefCache.invalidate();
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      return createTeamDefinition(GUID.create(), name, AtsUtilClient.createConfigObjectUuid(), changes, services);
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String guid, String name, long uuid, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      IAtsTeamDefinition item = teamDefFactory.createTeamDefinition(guid, name, uuid, changes, services);
      IAtsCache cache = atsCache();
      cache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      return createActionableItem(GUID.create(), name, AtsUtilClient.createConfigObjectUuid(), changes, services);
   }

   @Override
   public IAtsActionableItem createActionableItem(String guid, String name, long uuid, IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      IAtsActionableItem item = actionableItemFactory.createActionableItem(guid, name, uuid, changes, services);
      IAtsCache cache = atsCache();
      cache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsWorkDefinitionAdmin getWorkDefinitionAdmin() throws OseeStateException {
      return workDefAdmin;
   }

   @Override
   public IAtsClientVersionService getVersionService() throws OseeStateException {
      return versionService;
   }

   @Override
   public IAtsUserServiceClient getUserServiceClient() throws OseeStateException {
      return userServiceClient;
   }

   @Override
   public IAtsUserService getUserService() throws OseeStateException {
      return userServiceClient;
   }

   private IAtsCache atsCache() throws OseeCoreException {
      return atsCache;
   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(ArtifactId artifact) throws OseeCoreException {
      if (artifact instanceof Artifact) {
         return (Artifact) artifact;
      }
      return ArtifactQuery.getArtifactFromId(artifact, AtsUtilCore.getAtsBranch());
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
         result = ArtifactQuery.getArtifactFromId(uuid.intValue(), AtsUtilCore.getAtsBranch());
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
   public IAtsEarnedValueService getEarnedValueService() throws OseeStateException {
      return earnedValueService;
   }

   @Override
   public IAtsBranchService getBranchService() throws OseeCoreException {
      return branchService;
   }

   @Override
   public AbstractWorkflowArtifact getWorkflowArtifact(IAtsObject atsObject) throws OseeCoreException {
      return (AbstractWorkflowArtifact) getArtifact(atsObject);
   }

   @Override
   public IAtsReviewService getReviewService() throws OseeCoreException {
      return reviewService;
   }

   @Override
   public IAttributeResolver getAttributeResolver() {
      return attributeResolverService;
   }

   @Override
   public ISequenceProvider getSequenceProvider() {
      return sequenceProvider;
   }

   @Override
   public IAtsStateFactory getStateFactory() {
      if (stateFactory == null) {
         stateFactory = AtsCoreFactory.newStateFactory(getServices(), getLogFactory());
      }
      return stateFactory;
   }

   @Override
   public IAtsWorkStateFactory getWorkStateFactory() {
      if (workStateFactory == null) {
         workStateFactory = AtsCoreFactory.getWorkStateFactory(getUserService());
      }
      return workStateFactory;
   }

   @Override
   public IAtsLogFactory getLogFactory() {
      if (logFactory == null) {
         logFactory = AtsCoreFactory.getLogFactory();
      }
      return logFactory;
   }

   @Override
   public IAtsColumnService getColumnService() {
      if (columnServices == null) {
         columnServices = AtsCoreFactory.getColumnService(getServices());
      }
      return columnServices;
   }

   @Override
   public IAtsUtilService getUtilService() {
      return utilService;
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
   public String getConfigValue(String key) {
      String result = null;
      Artifact atsConfig = ArtifactQuery.getArtifactFromToken(AtsArtifactToken.AtsConfig, AtsUtilCore.getAtsBranch());
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
   public IAtsWorkDefinitionService getWorkDefService() {
      return workDefService;
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
   public IAtsActionFactory getActionFactory() {
      return actionFactory;
   }

   @Override
   public IAtsConfigItemFactory getConfigItemFactory() {
      return configItemFactory;
   }

   @Override
   public IRelationResolver getRelationResolver() {
      return relationResolver;
   }

   @Override
   public IAtsProgramService getProgramService() {
      return programService;
   }

   @Override
   public IAtsTeamDefinitionService getTeamDefinitionService() {
      return teamDefinitionService;
   }

   @Override
   public IAtsQueryService getQueryService() {
      return atsQueryService;
   }

   @Override
   public IAtsWorkItemFactory getWorkItemFactory() {
      return workItemFactory;
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
      Artifact result = null;
      try {
         result = ArtifactQuery.getArtifactFromAttribute(AtsAttributeTypes.AtsId, id, AtsUtilCore.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public Artifact getArtifactByGuid(String guid) throws OseeCoreException {
      return ArtifactQuery.getArtifactFromId(guid, AtsUtilCore.getAtsBranch());
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
      return ArtifactQuery.checkArtifactFromId((int) uuid, AtsUtilCore.getAtsBranch(), DeletionFlag.EXCLUDE_DELETED);
   }

   @Override
   public IAtsStoreService getStoreService() {
      return atsStoreService;
   }

   @Override
   public TeamWorkflowProviders getTeamWorkflowProviders() {
      return teamWorkflowProvidersLazy;
   }

   @Override
   public <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz) throws OseeCoreException {
      return getCache().getByUuid(uuid, clazz);
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
   public IArtifactResolver getArtifactResolver() {
      return artifactResolver;
   }

   @Override
   public IAtsTaskService getTaskService() {
      return taskService;
   }

   @Override
   public ArtifactId getArtifactByName(IArtifactType artType, String name) {
      return ArtifactQuery.getArtifactFromTypeAndNameNoException(artType, name, AtsUtilCore.getAtsBranch());
   }

   @Override
   public List<IAtsSearchDataProvider> getSearchDataProviders() {
      return searchDataProviders;
   }

   @Override
   public Log getLogger() {
      return logger;
   }

   @Override
   public IAtsEventService getEventService() {
      return eventService;
   }

   @Override
   public IAtsEarnedValueServiceProvider getEarnedValueServiceProvider() {
      return this;
   }

   @Override
   public IAtsImplementerService getImplementerService() {
      if (implementerService == null) {
         implementerService = new AtsImplementersService(this);
      }
      return implementerService;
   }

   @Override
   public IAtsCache getCache() {
      return atsCache;
   }

   @Override
   public AtsConfigurations getConfigurations() {
      return configProvider.getConfigurations();
   }

   @Override
   public void clearConfigurationsCaches() {
      configProvider.clearConfigurationsCaches();
   }

   @Override
   public IOseeBranch getAtsBranch() {
      return AtsUtilCore.getAtsBranch();
   }
   
   @Override
   public IAtsChangeSet createAtsChangeSet(String comment) {
      return getStoreService().createAtsChangeSet(comment, getUserService().getCurrentUser());
   }
}
