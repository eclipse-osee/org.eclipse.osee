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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.config.JaxActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsEventService;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.WorkDefData;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.agile.AgileService;
import org.eclipse.osee.ats.core.ai.ActionableItemService;
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
import org.eclipse.osee.ats.core.client.util.AtsClientUtilImpl;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.util.IArtifactMembersCache;
import org.eclipse.osee.ats.core.client.util.IAtsClientUtil;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionListeners;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.TeamDefinition;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsApiImpl;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionServiceImpl;
import org.eclipse.osee.ats.core.workflow.WorkItemFactory;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public class AtsClientImpl extends AtsApiImpl implements IAtsClient {

   private IActionableItemFactory actionableItemFactory;
   private ITeamDefinitionFactory teamDefFactory;

   private ArtifactCollectorsCache<GoalArtifact> goalMembersCache;
   private ArtifactCollectorsCache<SprintArtifact> sprintItemsCache;
   private IAtsEventService eventService;
   private IAgileService agileService;
   private IAtsClientUtil clientUtils;

   public AtsClientImpl() {
      super();
   }

   public void setConfigurationsService(IAtsConfigurationsService configurationsService) {
      this.configurationsService = configurationsService;
   }

   @Override
   public void start() {
      attributeResolverService = new AtsAttributeResolverServiceImpl();

      super.start();

      // ATS Client loads it Work Definitions configurations
      workDefinitionService = new AtsWorkDefinitionServiceImpl(this, workDefinitionStore, this,
         workDefinitionDslService, teamWorkflowProvidersLazy);

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
      storeService = new AtsStoreService(this, workItemFactory, getUserServiceClient(), jdbcService);

      queryService = new AtsQueryServiceImpl(this, jdbcService);
      actionableItemManager = new ActionableItemService(attributeResolverService, storeService, this);

      actionFactory = new ActionFactory(workItemFactory, sequenceProvider, actionableItemManager,
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
   public List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects) {
      List<Artifact> results = new LinkedList<>();
      for (ArtifactId artId : AtsObjects.getArtifacts(atsObjects)) {
         if (artId instanceof Artifact) {
            results.add((Artifact) artId);
         } else {
            Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(artId);
            if (artifact != null) {
               results.add(artifact);
            }
         }
      }
      return results;
   }

   @Override
   public void reloadWorkDefinitionCache(boolean pend) {
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            getWorkDefinitionService().reloadAll();
         }
      };
      if (pend) {
         reload.run();
      } else {
         new Thread(reload).start();
      }
   }

   @Override
   public void reloadUserCache(boolean pend) {
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            configurationsService.getConfigurationsWithPend();
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
   public void reloadAllCaches(boolean pend) {
      reloadUserCache(pend);
      reloadWorkDefinitionCache(pend);
      reloadConfigCache(pend);
   }

   @Override
   public void reloadConfigCache(boolean pend) {
      final AtsApi client = this;
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            try {
               // load artifacts to ensure they're in ArtifactCache prior to ATS chaching
               ArtifactQuery.getArtifactListFromTypeWithInheritence(AtsArtifactTypes.AtsConfigObject, getAtsBranch(),
                  DeletionFlag.EXCLUDE_DELETED);

               AtsConfigurations configs = getConfigService().getConfigurations();
               cacheActionableItems(configs.getIdToAi().get(configs.getTopActionableItem().getId()));
               cacheTeamDefinitions(configs.getIdToTeamDef().get(configs.getTopTeamDefinition().getId()));
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

         private void cacheTeamDefinitions(JaxTeamDefinition jaxTeamDef) {
            atsCache.cacheAtsObject(new TeamDefinition(getLogger(), client, jaxTeamDef));
            for (Long childId : jaxTeamDef.getChildren()) {
               cacheTeamDefinitions(getConfigService().getConfigurations().getIdToTeamDef().get(childId));
            }
         }

         private void cacheActionableItems(JaxActionableItem jaxAi) {
            atsCache.cacheAtsObject(new ActionableItem(getLogger(), client, jaxAi));
            for (Long child : jaxAi.getChildren()) {
               cacheActionableItems(getConfigService().getConfigurations().getIdToAi().get(child));
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
   public void clearCaches() {
      // clear client config cache (read from server)
      getConfigService().getConfigurations();

      super.clearCaches();
      getWorkDefinitionService().clearCaches();

      if (goalMembersCache != null) {
         goalMembersCache.invalidate();
      }
      if (sprintItemsCache != null) {
         sprintItemsCache.invalidate();
      }
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, IAtsChangeSet changes, AtsApi atsApi) {
      return createTeamDefinition(name, AtsUtilClient.createConfigObjectId(), changes, atsApi);
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, long id, IAtsChangeSet changes, AtsApi atsApi) {
      IAtsTeamDefinition item = teamDefFactory.createTeamDefinition(name, id, changes, atsApi);
      atsCache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, IAtsChangeSet changes, AtsApi atsApi) {
      return createActionableItem(name, AtsUtilClient.createConfigObjectId(), changes, atsApi);
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, long id, IAtsChangeSet changes, AtsApi atsApi) {
      IAtsActionableItem item = actionableItemFactory.createActionableItem(name, id, changes, atsApi);
      atsCache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsVersionService getVersionService() {
      return versionService;
   }

   @Override
   public IAtsUserServiceClient getUserServiceClient() {
      return (IAtsUserServiceClient) userService;
   }

   @Override
   public IAtsWorkItemService getWorkItemService() {
      return workItemService;
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
      Artifact atsConfig = ArtifactQuery.getArtifactFromToken(AtsArtifactToken.AtsConfig);
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
   public AtsApi getServices() {
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
   public Collection<ITransitionListener> getTransitionListeners() {
      return TransitionListeners.getListeners();
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) getQueryService().getArtifact(workItem);
      if (awa != null) {
         awa.clearImplementersCache();
      }
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
   public IAgileService getAgileService() {
      return agileService;
   }

   @Override
   public List<WorkDefData> getWorkDefinitionsData() {
      return getConfigService().getConfigurations().getWorkDefinitionsData();
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(ArtifactToken configToken) {
      return (T) getConfigItemFactory().getConfigObject(configToken);
   }

   @Override
   public <T> Collection<T> getConfigItems(ArtifactToken... configTokens) {
      List<T> results = new LinkedList<>();
      for (ArtifactToken art : configTokens) {
         T configItem = getConfigItem(art);
         if (configItem != null) {
            results.add(configItem);
         }
      }
      return results;
   }

   @Override
   public String getApplicationServerBase() {
      return OseeClientProperties.getOseeApplicationServer();
   }

   @Override
   public IAtsActionableItemService getActionableItemService() {
      return actionableItemManager;
   }

   /**
    * This should only be called by tests that require it or in a single server deployment (such as the demo dbinit).
    */
   @Override
   public void reloadServerAndClientCaches() {
      AtsClientService.getConfigEndpoint().getWithPend();
      AtsClientService.get().getConfigService().getConfigurations();
   }

   @Override
   public IAtsClientUtil getClientUtils() {
      if (clientUtils == null) {
         clientUtils = new AtsClientUtilImpl();
      }
      return clientUtils;
   }

}