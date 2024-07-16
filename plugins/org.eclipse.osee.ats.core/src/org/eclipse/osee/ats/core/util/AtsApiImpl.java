/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.access.IAtsAccessService;
import org.eclipse.osee.ats.api.agile.IAgileSprintHtmlOperation;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.event.IAtsEventService;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.task.create.IAtsTaskSetDefinitionProviderService;
import org.eclipse.osee.ats.api.task.related.IAtsTaskRelatedService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsActionService;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsDatabaseTypeProvider;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.access.AtsAccessService;
import org.eclipse.osee.ats.core.action.AtsActionService;
import org.eclipse.osee.ats.core.config.TeamDefinitionServiceImpl;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;
import org.eclipse.osee.ats.core.internal.state.AtsStateFactory;
import org.eclipse.osee.ats.core.internal.state.AtsWorkStateFactory;
import org.eclipse.osee.ats.core.program.AtsProgramService;
import org.eclipse.osee.ats.core.review.AtsReviewServiceImpl;
import org.eclipse.osee.ats.core.task.internal.AtsTaskSetDefinitionProviderService;
import org.eclipse.osee.ats.core.version.AtsVersionServiceImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionServiceImpl;
import org.eclipse.osee.ats.core.workflow.AtsImplementersService;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.OseeApiBase;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsApiImpl extends OseeApiBase implements AtsApi {

   private static final Object lock = new Object();
   private volatile static BranchToken atsBranch;
   private static final String ATS_BRANCH_NAME = "ats.branch.name";
   private static final String ATS_BRANCH_ID = "ats.branch.id";
   private final List<IAtsSearchDataProvider> searchDataProviders;
   protected Log logger;
   protected JdbcService jdbcService;
   protected IAtsWorkDefinitionService workDefinitionService;
   protected IAtsUserService userService;
   protected IAtsConfigurationsService configurationsService;
   protected IAtsEarnedValueService earnedValueService;
   protected TeamWorkflowProviders teamWorkflowProvidersLazy;
   protected IAttributeResolver attributeResolverService;
   protected IAtsActionService actionService;
   protected IAtsImplementerService implementerService;
   protected IAtsWorkItemService workItemService;
   protected ISequenceProvider sequenceProvider;
   protected IAtsProgramService programService;
   protected IAtsStateFactory stateFactory;
   protected IArtifactResolver artifactResolver;
   protected IAtsBranchService branchService;
   protected IAtsReviewService reviewService;
   protected IAtsWorkStateFactory workStateFactory;
   protected IAtsColumnService columnServices;
   protected IAtsActionableItemService actionableItemManager;
   protected IRelationResolver relationResolver;
   protected IAtsVersionService versionService;
   protected IAtsTaskService taskService;
   protected IAtsTeamDefinitionService teamDefinitionService;
   protected IAtsQueryService queryService;
   protected IAtsStoreService storeService;
   protected IAtsTaskRelatedService taskRelatedService;
   protected IAtsWorkDefinitionProviderService workDefinitionProviderService;
   protected IAtsEventService eventService;
   protected EventAdmin eventAdmin;
   protected IAtsLogFactory logFactory;
   protected IAtsTaskSetDefinitionProviderService taskSetDefinitionProviderService;
   protected IAtsNotificationService notificationService;
   protected List<IAtsDatabaseTypeProvider> databaseTypeProviders = new LinkedList<>();
   protected IAtsAccessService atsAccessService;

   Collection<IAgileSprintHtmlOperation> agileSprintHtmlReportOperations = new LinkedList<>();

   public AtsApiImpl() {
      searchDataProviders = new ArrayList<>();
   }

   public void setAtsEventService(IAtsEventService eventService) {
      this.eventService = eventService;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setEventAdmin(EventAdmin eventAdmin) {
      this.eventAdmin = eventAdmin;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setWorkDefinitionProviderService(IAtsWorkDefinitionProviderService workDefinitionProviderService) {
      this.workDefinitionProviderService = workDefinitionProviderService;
   }

   public void setAtsUserService(IAtsUserService userServiceClient) {
      this.userService = userServiceClient;
   }

   public void addSearchDataProvider(IAtsSearchDataProvider provider) {
      searchDataProviders.add(provider);
   }

   public void removeSearchDataProvider(IAtsSearchDataProvider provider) {
      searchDataProviders.remove(provider);
   }

   public void setTaskSetDefinitionProviderService(IAtsTaskSetDefinitionProviderService taskSetDefinitionProviderService) {
      this.taskSetDefinitionProviderService = taskSetDefinitionProviderService;
   }

   public void addDatabaseTypeProvider(IAtsDatabaseTypeProvider provider) {
      databaseTypeProviders.add(provider);
   }

   @Override
   public List<IAtsDatabaseTypeProvider> getDatabaseTypeProviders() {
      return databaseTypeProviders;
   }

   public void start() {

      teamWorkflowProvidersLazy = new TeamWorkflowProviders();
      workItemService = new AtsWorkItemServiceImpl(this, teamWorkflowProvidersLazy);

      programService = new AtsProgramService(this);
      teamDefinitionService = new TeamDefinitionServiceImpl(this);
      versionService = new AtsVersionServiceImpl(this, eventAdmin);
      reviewService = new AtsReviewServiceImpl(this);

      workDefinitionService = new AtsWorkDefinitionServiceImpl(this, teamWorkflowProvidersLazy);
      logFactory = new AtsLogFactory();
      actionService = new AtsActionService(this);
   }

   public void stop() {
      workDefinitionService = null;
      jdbcService = null;
   }

   @Override
   public void clearCaches() {
      userService.reloadCache();
   }

   @Override
   public String getAtsId(ArtifactToken artifact) {
      return getAtsId(getAttributeResolver(), artifact);
   }

   @Override
   public String getAtsId(IAtsObject atsObject) {
      return getAtsId(getAttributeResolver(), atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, IAtsObject atsObject) {
      return getAtsId(attrResolver, atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, ArtifactToken artifact) {
      Conditions.checkNotNull(artifact, "artifact");
      String toReturn = attrResolver.getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId, null);
      if (toReturn == null) {
         toReturn = artifact.getIdString();
      }
      return toReturn;
   }

   @Override
   public String getConfigValue(String key, String defaultValue) {
      String value;
      try {
         value = getConfigValue(key);
         if (Strings.isInValid(value)) {
            value = defaultValue;
         }
      } catch (Exception ex) {
         value = defaultValue;
      }
      return value;
   }

   @Override
   public void setUserConfigValue(String key, String value) {
      ArtifactId userArt = getUserService().getCurrentUser();
      IAtsChangeSet changes =
         storeService.createAtsChangeSet("Set User AtsConfig Value", getUserService().getCurrentUser());
      if (userArt != null) {
         String keyValue = String.format("%s=%s", key, value);
         boolean found = false;
         Collection<IAttribute<String>> attributes =
            getAttributeResolver().getAttributes(userArt, CoreAttributeTypes.AtsUserConfig);
         for (IAttribute<String> attr : attributes) {
            String str = attr.getValue();
            if (str.startsWith(key)) {
               changes.setAttribute(userArt, attr, keyValue);
               found = true;
               break;
            }
         }
         if (!found) {
            changes.addAttribute(userArt, CoreAttributeTypes.AtsUserConfig, keyValue);
         }
         changes.executeIfNeeded();
      }
   }

   @Override
   public void setConfigValue(String key, String value) {
      ArtifactId atsConfig = getQueryService().getArtifact(AtsArtifactToken.AtsConfig);
      IAtsChangeSet changes = storeService.createAtsChangeSet("Set AtsConfig Value", getUserService().getCurrentUser());
      if (atsConfig != null) {
         String keyValue = String.format("%s=%s", key, value);
         boolean found = false;
         Collection<IAttribute<String>> attributes =
            getAttributeResolver().getAttributes(atsConfig, CoreAttributeTypes.GeneralStringData);
         for (IAttribute<String> attr : attributes) {
            String str = attr.getValue();
            if (str.startsWith(key)) {
               changes.setAttribute(atsConfig, attr, keyValue);
               found = true;
               break;
            }
         }
         if (!found) {
            changes.addAttribute(atsConfig, CoreAttributeTypes.GeneralStringData, keyValue);
         }
         changes.executeIfNeeded();
      }
   }

   @Override
   public BranchToken getAtsBranch() {
      synchronized (lock) {
         if (atsBranch == null) {
            // Preference store overrides all
            if (AtsPreferencesService.isAvailable()) {
               try {
                  String atsBranchId = AtsPreferencesService.get(ATS_BRANCH_ID);
                  setConfig(atsBranchId, AtsPreferencesService.get(ATS_BRANCH_NAME));
               } catch (Exception ex) {
                  OseeLog.log(AtsUtil.class, Level.SEVERE, "Error processing stored ATS Branch.", ex);
               }
            }
            // osee.ini -D option overrides default
            if (atsBranch == null) {
               String atsBranchId = System.getProperty(ATS_BRANCH_ID);
               if (Strings.isValid(atsBranchId)) {
                  setConfig(atsBranchId, System.getProperty(ATS_BRANCH_NAME));
               }
            }
            // default is always common
            if (atsBranch == null) {
               atsBranch = CoreBranches.COMMON;
            }
         }
      }
      return atsBranch;
   }

   private void setConfig(String branchId, String name) {
      if (!Strings.isValid(name)) {
         name = "unknown";
      }
      if (Strings.isValid(branchId) && branchId.matches("\\d+")) {
         atsBranch = BranchToken.create(Long.valueOf(branchId), name);
      }
   }

   @Override
   public void storeAtsBranch(BranchId branch, String name) {
      AtsPreferencesService.get().put(ATS_BRANCH_ID, branch.getIdString());
      AtsPreferencesService.get().put(ATS_BRANCH_NAME, name);
   }

   @Override
   public IAtsSearchDataProvider getSearchDataProvider(String namespace) {
      for (IAtsSearchDataProvider provider : searchDataProviders) {
         if (provider.supportsNamespace(namespace)) {
            return provider;
         }
      }
      throw new OseeArgumentException("Namespace [%s] is not supported by any of the providers %s", namespace,
         searchDataProviders);
   }

   @Override
   public Log getLogger() {
      return logger;
   }

   @Override
   public IAtsEarnedValueService getEarnedValueService() {
      return earnedValueService;
   }

   @Override
   public IAtsWorkDefinitionService getWorkDefinitionService() {
      return workDefinitionService;
   }

   @Override
   public IAtsUserService getUserService() {
      return userService;
   }

   @Override
   public IAttributeResolver getAttributeResolver() {
      return attributeResolverService;
   }

   @Override
   public IAtsEarnedValueServiceProvider getEarnedValueServiceProvider() {
      return this;
   }

   @Override
   public ITeamWorkflowProvidersLazy getTeamWorkflowProviders() {
      return teamWorkflowProvidersLazy;
   }

   @Override
   public IAtsActionService getActionService() {
      return actionService;
   }

   @Override
   public IAtsImplementerService getImplementerService() {
      if (implementerService == null) {
         implementerService = new AtsImplementersService();
      }
      return implementerService;
   }

   @Override
   public IAtsWorkItemService getWorkItemService() {
      return workItemService;
   }

   public void setWorkItemService(IAtsWorkItemService workItemService) {
      this.workItemService = workItemService;
   }

   @Override
   public ISequenceProvider getSequenceProvider() {
      if (sequenceProvider == null) {
         sequenceProvider = new ISequenceProvider() {

            @Override
            public long getNext(String sequenceName) {
               // Sequence is set to sequential
               return jdbcService.getClient().getNextSequence(sequenceName, false);
            }
         };
      }
      return sequenceProvider;
   }

   @Override
   public IAtsProgramService getProgramService() {
      return programService;
   }

   @Override
   public IAtsReviewService getReviewService() {
      return reviewService;
   }

   @Override
   public IAtsStateFactory getStateFactory() {
      if (stateFactory == null) {
         stateFactory = new AtsStateFactory(this, new AtsWorkStateFactory(userService), new AtsLogFactory());
      }
      return stateFactory;
   }

   @Override
   public IAtsWorkStateFactory getWorkStateFactory() {
      if (workStateFactory == null) {
         workStateFactory = new AtsWorkStateFactory(userService);
      }
      return workStateFactory;
   }

   @Override
   public IAtsLogFactory getLogFactory() {
      return logFactory;
   }

   @Override
   public IArtifactResolver getArtifactResolver() {
      return artifactResolver;
   }

   @Override
   public IRelationResolver getRelationResolver() {
      return relationResolver;
   }

   @Override
   public IAtsBranchService getBranchService() {
      return branchService;
   }

   @Override
   public IAtsColumnService getColumnService() {
      if (columnServices == null) {
         columnServices = new AtsColumnService(this);
      }
      return columnServices;
   }

   @Override
   public IAtsVersionService getVersionService() {
      return versionService;
   }

   public void setVersionService(IAtsVersionService versionService) {
      this.versionService = versionService;
   }

   @Override
   public IAtsTaskService getTaskService() {
      return taskService;
   }

   @Override
   public IAtsTeamDefinitionService getTeamDefinitionService() {
      return teamDefinitionService;
   }

   @Override
   public IAtsQueryService getQueryService() {
      return queryService;
   }

   @Override
   public IAtsStoreService getStoreService() {
      return storeService;
   }

   @Override
   public IAtsChangeSet createChangeSet(String comment, AtsUser user) {
      return storeService.createAtsChangeSet(comment, user);
   }

   @Override
   public JdbcService getJdbcService() {
      return jdbcService;
   }

   @Override
   public Collection<IAgileSprintHtmlOperation> getAgileSprintHtmlReportOperations() {
      return agileSprintHtmlReportOperations;
   }

   @Override
   public boolean isSingleServerDeployment() {
      return "true".equals(getConfigValue(AtsUtil.SINGLE_SERVER_DEPLOYMENT));
   }

   @Override
   public IAtsConfigurationsService getConfigService() {
      return configurationsService;
   }

   @Override
   public IAtsWorkDefinitionProviderService getWorkDefinitionProviderService() {
      return workDefinitionProviderService;
   }

   @Override
   public String getUserConfigValue(String key) {
      String result = null;
      ArtifactToken userArt = userService().getUser();
      if (userArt != null) {
         for (String configKeyValueStr : getAttributeResolver().getAttributesToStringList(userArt,
            CoreAttributeTypes.AtsUserConfig)) {
            if (configKeyValueStr.startsWith(key)) {
               result = configKeyValueStr.replaceFirst(key + "=", "");
               break;
            }
         }
      }
      return result;
   }

   @Override
   public EventAdmin getEventAdmin() {
      return eventAdmin;
   }

   @Override
   public IAtsEventService getEventService() {
      return eventService;
   }

   @Override
   public IAtsTaskSetDefinitionProviderService getTaskSetDefinitionProviderService() {
      if (taskSetDefinitionProviderService == null) {
         taskSetDefinitionProviderService = new AtsTaskSetDefinitionProviderService();
      }
      return taskSetDefinitionProviderService;
   }

   @Override
   public IAtsChangeSet createChangeSet(String comment, BranchToken branch) {
      return storeService.createAtsChangeSet(comment, branch, userService.getCurrentUser());
   }

   @Override
   public IAtsChangeSet createChangeSet(String comment, BranchToken branch, AtsUser asUser) {
      return storeService.createAtsChangeSet(comment, branch, asUser);
   }

   @Override
   public IAtsChangeSet createChangeSet(String comment) {
      return storeService.createAtsChangeSet(comment, userService.getCurrentUser());
   }

   @Override
   public String getConfigValue(String key) {
      return getConfigService().getConfigurations().getConfigValue(key);
   }

   @Override
   public IAtsNotificationService getNotificationService() {
      return notificationService;
   }

   @Override
   public IAtsAccessService getAtsAccessService() {
      if (atsAccessService == null) {
         atsAccessService = new AtsAccessService(this);
      }
      return atsAccessService;
   }

}
