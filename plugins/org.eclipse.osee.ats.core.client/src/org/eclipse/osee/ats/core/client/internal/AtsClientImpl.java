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
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
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
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.client.util.IArtifactMembersCache;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionListeners;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.TeamDefinition;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsCoreServiceImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionServiceImpl;
import org.eclipse.osee.ats.core.workflow.WorkItemFactory;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.OsgiUtil;
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
   public void start()  {
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
   public Artifact getConfigArtifact(IAtsConfigObject atsConfigObject)  {
      return AtsClientService.get().getArtifact(atsConfigObject);
   }

   @Override
   public List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects)  {
      List<Artifact> results = new LinkedList<>();
      for (ArtifactId artId : AtsObjects.getArtifacts(atsObjects)) {
         if (artId instanceof Artifact) {
            results.add((Artifact) artId);
         } else {
            Artifact artifact = AtsClientService.get().getArtifact(artId);
            if (artifact != null) {
               results.add(artifact);
            }
         }
      }
      return results;
   }

   @Override
   public void reloadWorkDefinitionCache(boolean pend)  {
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
   public void reloadUserCache(boolean pend)  {
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
   public void reloadAllCaches(boolean pend)  {
      reloadUserCache(pend);
      reloadWorkDefinitionCache(pend);
      reloadConfigCache(pend);
   }

   @Override
   public void reloadConfigCache(boolean pend) {
      final IAtsServices client = this;
      Runnable reload = new Runnable() {

         @Override
         public void run() {
            try {
               // load artifacts to ensure they're in ArtifactCache prior to ATS chaching
               ArtifactQuery.getArtifactListFromTypeWithInheritence(AtsArtifactTypes.AtsConfigObject, getAtsBranch(),
                  DeletionFlag.EXCLUDE_DELETED);

               cacheActionableItems(configProvider.getConfigurations().getIdToAi().get(
                  configProvider.getConfigurations().getTopActionableItem().getId()));
               cacheTeamDefinitions(configProvider.getConfigurations().getIdToTeamDef().get(
                  configProvider.getConfigurations().getTopTeamDefinition().getId()));
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

         private void cacheTeamDefinitions(JaxTeamDefinition jaxTeamDef) {
            atsCache.cacheAtsObject(new TeamDefinition(getLogger(), client, jaxTeamDef));
            for (Long childId : jaxTeamDef.getChildren()) {
               cacheTeamDefinitions(configProvider.getConfigurations().getIdToTeamDef().get(childId));
            }
         }

         private void cacheActionableItems(JaxActionableItem jaxAi) {
            atsCache.cacheAtsObject(new ActionableItem(getLogger(), client, jaxAi));
            for (Long child : jaxAi.getChildren()) {
               cacheActionableItems(configProvider.getConfigurations().getIdToAi().get(child));
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
      // clear server config cache
      AtsClientService.getConfigEndpoint().clearCaches();

      // clear client config cache (read from server)
      clearConfigurationsCaches();

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
   public IAtsTeamDefinition createTeamDefinition(String name, IAtsChangeSet changes, IAtsServices services)  {
      return createTeamDefinition(name, AtsUtilClient.createConfigObjectUuid(), changes, services);
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, long uuid, IAtsChangeSet changes, IAtsServices services)  {
      IAtsTeamDefinition item = teamDefFactory.createTeamDefinition(name, uuid, changes, services);
      atsCache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, IAtsChangeSet changes, IAtsServices services)  {
      return createActionableItem(name, AtsUtilClient.createConfigObjectUuid(), changes, services);
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, long uuid, IAtsChangeSet changes, IAtsServices services)  {
      IAtsActionableItem item = actionableItemFactory.createActionableItem(name, uuid, changes, services);
      atsCache.cacheAtsObject(item);
      return item;
   }

   @Override
   public IAtsVersionService getVersionService()  {
      return versionService;
   }

   @Override
   public IAtsUserServiceClient getUserServiceClient()  {
      return (IAtsUserServiceClient) userService;
   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(ArtifactId artifact)  {
      if (artifact instanceof Artifact) {
         return (Artifact) artifact;
      }
      try {
         return ArtifactQuery.getArtifactFromId(artifact, getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         return null;
      }
   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(IAtsObject atsObject)  {
      Artifact results = null;
      if (atsObject.getStoreObject() != null) {
         if (atsObject.getStoreObject() instanceof Artifact) {
            results = (Artifact) atsObject.getStoreObject();
         } else {
            results = AtsClientService.get().getArtifact(atsObject.getId());
            if (results != null) {
               atsObject.setStoreObject(results);
            }
         }
      } else {
         results = getArtifact(atsObject.getId());
         if (results != null) {
            atsObject.setStoreObject(results);
         }
      }
      return results;
   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(Long uuid)  {
      Conditions.checkExpressionFailOnTrue(uuid <= 0, "Uuid must be > 0; is %d", uuid);
      Artifact result = null;
      try {
         result = ArtifactQuery.getArtifactFromId(uuid, getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return result;
   }

   @Override
   public IAtsWorkItemService getWorkItemService()  {
      return workItemService;
   }

   @Override
   public AbstractWorkflowArtifact getWorkflowArtifact(IAtsObject atsObject)  {
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
   public Artifact getArtifactByGuid(String guid)  {
      return ArtifactQuery.getArtifactFromId(guid, getAtsBranch());
   }

   @Override
   public Artifact getArtifactByLegacyPcrId(String id) {
      return (Artifact) super.getArtifactByLegacyPcrId(id);
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
   public <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz)  {
      return getCache().getAtsObject(uuid);
   }

   @Override
   public Collection<ITransitionListener> getTransitionListeners() {
      return TransitionListeners.getListeners();
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) getArtifact(workItem);
      if (awa != null) {
         awa.clearImplementersCache();
      }
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
   public Collection<ArtifactToken> getArtifacts(Collection<Long> ids) {
      List<ArtifactId> artifactIds = new ArrayList<>(ids.size());
      for (Long id : ids) {
         artifactIds.add(ArtifactId.valueOf(id));
      }
      return Collections.castAll(
         ArtifactQuery.getArtifactListFrom(artifactIds, getAtsBranch(), DeletionFlag.EXCLUDE_DELETED));
   }

   @Override
   public IAgileService getAgileService() {
      return agileService;
   }

   @Override
   public List<WorkDefData> getWorkDefinitionsData() {
      return getConfigurations().getWorkDefinitionsData();
   }

   @Override
   public void clearConfigurationsCaches() {
      configProvider.clearConfigurationsCaches();
   }

   @Override
   public Artifact getArtifact(ArtifactId artifact, BranchId branch) {
      return ArtifactQuery.getArtifactFromId(artifact, branch);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(ArtifactToken configToken) {
      ArtifactId artifact = getArtifact(configToken.getId());
      if (artifact != null) {
         IAtsConfigObject configObject = getConfigItemFactory().getConfigObject(artifact);
         return (T) configObject;
      }
      return null;
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
   public Collection<ArtifactToken> getArtifacts(IArtifactType artifactType) {
      return Collections.castAll(ArtifactQuery.getArtifactListFromType(artifactType, getAtsBranch()));
   }

   @Override
   public IAtsActionableItemService getActionableItemService() {
      return actionableItemManager;
   }

}