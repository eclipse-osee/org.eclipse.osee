/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.task.related.IAtsTaskRelatedService;
import org.eclipse.osee.ats.api.util.IAtsHealthService;
import org.eclipse.osee.ats.api.util.IAtsServerEndpointProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.agile.AgileService;
import org.eclipse.osee.ats.core.ai.ActionableItemServiceImpl;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsApiImpl;
import org.eclipse.osee.ats.ide.access.AtsBranchAccessManager;
import org.eclipse.osee.ats.ide.branch.AtsBranchServiceIde;
import org.eclipse.osee.ats.ide.branch.internal.AtsBranchServiceIdeImpl;
import org.eclipse.osee.ats.ide.branch.internal.AtsBranchServiceImpl;
import org.eclipse.osee.ats.ide.ev.internal.AtsEarnedValueImpl;
import org.eclipse.osee.ats.ide.health.AtsHealthServiceImpl;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.query.AtsQueryServiceIde;
import org.eclipse.osee.ats.ide.search.internal.query.AtsQueryServiceImpl;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.util.AtsServerEndpointProviderImpl;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.util.IArtifactMembersCache;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.AtsWorkItemServiceClientImpl;
import org.eclipse.osee.ats.ide.workflow.IAtsWorkItemServiceIde;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.internal.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.ide.workflow.internal.AtsRelationResolverServiceImpl;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.workflow.task.IAtsTaskServiceIde;
import org.eclipse.osee.ats.ide.workflow.task.internal.AtsTaskService;
import org.eclipse.osee.ats.ide.workflow.task.related.AtsTaskRelatedService;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public class AtsApiIdeImpl extends AtsApiImpl implements AtsApiIde {

   private ArtifactCollectorsCache<GoalArtifact> goalMembersCache;
   private ArtifactCollectorsCache<SprintArtifact> sprintItemsCache;
   private IAgileService agileService;
   private AtsQueryServiceIde queryServiceIde;
   private IAtsWorkItemServiceIde workItemServiceIde;
   private IAtsServerEndpointProvider serverEndpoints;
   private AtsBranchServiceIde branchServiceIde;

   public void setConfigurationsService(IAtsConfigurationsService configurationsService) {
      this.configurationsService = configurationsService;
      this.configurationsService.setAtsApi(this);
      Job loadAtsConfig = new Job("Load ATS Configs") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            configurationsService.getConfigurationsWithPend();
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(loadAtsConfig);
   }

   // for ReviewOsgiXml public void setJdbcService(JdbcService jdbcService)
   // for ReviewOsgiXml public void setEventAdmin(EventAdmin eventAdmin)
   // for ReviewOsgiXml public void setLogger(Log logger)
   // for ReviewOsgiXml public void setAtsEventService(IAtsEventService eventService)
   // for ReviewOsgiXml public void setWorkDefinitionProviderService(IAtsWorkDefinitionProviderService workDefinitionProviderService)
   // for ReviewOsgiXml public void setAtsUserService(IAtsUserService userServiceClient)
   // for ReviewOsgiXml public void addSearchDataProvider(IAtsSearchDataProvider provider)
   // for ReviewOsgiXml public void removeSearchDataProvider(IAtsSearchDataProvider provider) {
   // for ReviewOsgiXml public void setTaskSetDefinitionProviderService(IAtsTaskSetDefinitionProviderService taskSetDefinitionProviderService)
   // for ReviewOsgiXml public void setOrcsTokenService(OrcsTokenService tokenService)
   // for ReviewOsgiXml public void setJaxRsApi(JaxRsApi jaxRsApi)

   @Override
   public void start() {
      attributeResolverService = new AtsAttributeResolverServiceImpl();

      super.start();

      earnedValueService = new AtsEarnedValueImpl(logger, this);

      artifactResolver = new ArtifactResolverImpl();
      relationResolver = new AtsRelationResolverServiceImpl(this);

      branchService = new AtsBranchServiceImpl(this, teamWorkflowProvidersLazy);

      storeService = new AtsStoreService(this, getUserService(), jdbcService);

      queryService = new AtsQueryServiceImpl(this, jdbcService);
      queryServiceIde = new AtsQueryServiceIde(this);
      actionableItemManager = new ActionableItemServiceImpl(attributeResolverService, this);

      actionFactory = new ActionFactory(attributeResolverService, this);
      taskService = new AtsTaskService(this);

      agileService = new AgileService(logger, this);

   }

   public void setAttributeResolverService(IAttributeResolver attributeResolverService) {
      this.attributeResolverService = attributeResolverService;
   }

   @Override
   public void clearCaches() {
      // clear client config cache (read from server)
      getConfigService().getConfigurations();
      getUserService().clearCaches();

      super.clearCaches();

      if (goalMembersCache != null) {
         goalMembersCache.invalidate();
      }
      if (sprintItemsCache != null) {
         sprintItemsCache.invalidate();
      }

      AtsBranchAccessManager.clearCaches();
   }

   @Override
   public IAtsVersionService getVersionService() {
      return versionService;
   }

   @Override
   public IAtsWorkItemService getWorkItemService() {
      return workItemService;
   }

   @Override
   public synchronized void sendNotifications(final AtsNotificationCollector notifications) {
      if (AtsUtilClient.isEmailEnabled()) {
         AtsNotifyEndpointApi notifyEndpoint = AtsApiService.get().getServerEndpoints().getNotifyEndpoint();
         Jobs.startJob(new Job("Send Notifications") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               notifyEndpoint.sendNotifications(notifications);
               return Status.OK_STATUS;
            }
         }, false);
      }
   }

   @Override
   public synchronized void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String body) {
      throw new UnsupportedOperationException("Not supported on client");
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
   public Collection<ArtifactTypeToken> getArtifactTypes() {
      List<ArtifactTypeToken> types = new ArrayList<>();
      types.addAll(tokenService().getArtifactTypes());
      return types;
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
   public void clearImplementersCache(IAtsWorkItem workItem) {
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) getQueryService().getArtifact(workItem);
      if (awa != null) {
         awa.clearImplementersCache();
      }
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
   public String getApplicationServerBase() {
      return OseeClientProperties.getOseeApplicationServer();
   }

   @Override
   public IAtsActionableItemService getActionableItemService() {
      return actionableItemManager;
   }

   @Override
   public boolean isWorkDefAsName() {
      return "true".equals(OseeInfo.getCachedValue("osee.work.def.as.name"));
   }

   @Override
   public IAtsQueryService getQueryService() {
      return queryService;
   }

   @Override
   public IAtsTaskRelatedService getTaskRelatedService() {
      if (taskRelatedService == null) {
         taskRelatedService = new AtsTaskRelatedService(this);
      }
      return taskRelatedService;
   }

   @Override
   public AtsQueryServiceIde getQueryServiceIde() {
      return queryServiceIde;
   }

   @Override
   public IAtsHealthService getHealthService() {
      return new AtsHealthServiceImpl();
   }

   @Override
   public IAtsTaskServiceIde getTaskServiceIde() {
      return (IAtsTaskServiceIde) taskService;
   }

   @Override
   public IAtsWorkItemServiceIde getWorkItemServiceIde() {
      if (workItemServiceIde == null) {
         workItemServiceIde = new AtsWorkItemServiceClientImpl(this, teamWorkflowProvidersLazy);
      }
      return workItemServiceIde;
   }

   @Override
   public IAtsServerEndpointProvider getServerEndpoints() {
      if (serverEndpoints == null) {
         serverEndpoints = new AtsServerEndpointProviderImpl(this);
      }
      return serverEndpoints;
   }

   @Override
   public boolean isIde() {
      return true;
   }

   @Override
   public IUserGroupService getUserGroupService() {
      return UserGroupService.instance();
   }

   @Override
   public AtsBranchServiceIde getBranchServiceIde() {
      if (branchServiceIde == null) {
         branchServiceIde = new AtsBranchServiceIdeImpl();
      }
      return branchServiceIde;
   }
}