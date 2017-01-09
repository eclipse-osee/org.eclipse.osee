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
package org.eclipse.osee.ats.rest.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
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
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.ai.ActionableItemManager;
import org.eclipse.osee.ats.core.config.AtsCache;
import org.eclipse.osee.ats.core.program.AtsProgramService;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsCoreServiceImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionAdminImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionCache;
import org.eclipse.osee.ats.core.workflow.AtsImplementersService;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.agile.AgileService;
import org.eclipse.osee.ats.rest.internal.config.AtsConfigEndpointImpl;
import org.eclipse.osee.ats.rest.internal.convert.ConvertBaselineGuidToBaselineUuid;
import org.eclipse.osee.ats.rest.internal.convert.ConvertFavoriteBranchGuidToUuid;
import org.eclipse.osee.ats.rest.internal.notify.AtsNotificationEventProcessor;
import org.eclipse.osee.ats.rest.internal.notify.AtsNotifierServiceImpl;
import org.eclipse.osee.ats.rest.internal.notify.WorkItemNotificationProcessor;
import org.eclipse.osee.ats.rest.internal.query.AtsQueryServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.ArtifactResolverImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsBranchServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsEarnedValueImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsRelationResolverServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsReviewServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsStoreServiceImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTaskService;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTeamDefinitionService;
import org.eclipse.osee.ats.rest.internal.workitem.AtsVersionServiceImpl;
import org.eclipse.osee.ats.rest.internal.workitem.ConfigItemFactory;
import org.eclipse.osee.ats.rest.internal.workitem.WorkItemFactory;
import org.eclipse.osee.ats.rest.util.ChangeTypeUtil;
import org.eclipse.osee.ats.rest.util.IAtsNotifierServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Donald G Dunne
 */
public class AtsServerImpl extends AtsCoreServiceImpl implements IAtsServer {

   public static String PLUGIN_ID = "org.eclipse.osee.ats.rest";
   private OrcsApi orcsApi;
   private QueryFactory query;
   private Log logger;
   private IAtsWorkItemFactory workItemFactory;
   private IAtsWorkDefinitionService workDefService;
   private IAtsUserService userService;
   private AtsNotifierServiceImpl notifyService;
   private IAtsWorkItemService workItemService;
   private IAtsBranchService branchService;
   private IAtsReviewService reviewService;
   private IAtsWorkDefinitionAdmin workDefAdmin;
   private AtsWorkDefinitionCache workDefCache;
   private AtsAttributeResolverServiceImpl attributeResolverService;
   private IAtsCache atsCache;
   private IAtsConfigItemFactory configItemFactory;
   private IAtsLogFactory atsLogFactory;
   private IAtsStateFactory atsStateFactory;
   private IAtsStoreService atsStoreService;
   private IAtsUtilService utilService;
   private ISequenceProvider sequenceProvider;
   private IAtsActionFactory actionFactory;
   private IAtsActionableItemService actionableItemManager;
   private JdbcService jdbcService;
   private WorkItemNotificationProcessor workItemNotificationProcessor;
   private AtsNotificationEventProcessor notificationEventProcessor;
   private IAtsVersionService versionService;
   private IRelationResolver relationResolver;
   private IAtsProgramService atsProgramService;
   private IAtsTeamDefinitionService atsTeamDefinitionService;
   private JdbcClient jdbcClient;
   private IAgileService agileService;
   private IAtsQueryService atsQueryService;
   private IAtsTaskService taskService;
   private IAtsEarnedValueService earnedValueService;
   private IAtsImplementerService implementerService;

   private volatile boolean emailEnabled = true;
   private boolean loggedNotificationDisabled = false;

   private final List<IAtsNotifierServer> notifiers = new CopyOnWriteArrayList<>();
   private final Map<String, IAtsDatabaseConversion> externalConversions =
      new ConcurrentHashMap<String, IAtsDatabaseConversion>();
   private IArtifactResolver artifactResolver;
   private IAtsColumnService columnServices;
   private AtsConfigEndpointImpl configurationsProvider;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   @Override
   public IAtsConfigItemFactory getConfigItemFactory() {
      return configItemFactory;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      query = orcsApi.getQueryFactory();
   }

   public void setWorkDefService(IAtsWorkDefinitionService workDefService) {
      this.workDefService = workDefService;
   }

   public void setAtsUserService(IAtsUserService userService) {
      this.userService = userService;
   }

   @Override
   public void addAtsDatabaseConversion(IAtsDatabaseConversion conversion) {
      externalConversions.put(conversion.getName(), conversion);
   }

   public void removeAtsDatabaseConversion(IAtsDatabaseConversion conversion) {
      externalConversions.remove(conversion.getName());
   }

   public void addNotifier(IAtsNotifierServer notifier) {
      notifiers.add(notifier);
   }

   public void removeNotifier(IAtsNotifierServer notifier) {
      notifiers.remove(notifier);
   }

   public void start() throws OseeCoreException {
      jdbcClient = jdbcService.getClient();

      notifyService = new AtsNotifierServiceImpl();
      workItemFactory = new WorkItemFactory(logger, this);
      configItemFactory = new ConfigItemFactory(logger, this);

      TeamWorkflowProviders teamWorkflowProvidersLazy = new TeamWorkflowProviders();
      artifactResolver = new ArtifactResolverImpl(this, orcsApi);
      workItemService = new AtsWorkItemServiceImpl(this, teamWorkflowProvidersLazy);
      branchService = new AtsBranchServiceImpl(getServices(), orcsApi, teamWorkflowProvidersLazy);
      reviewService = new AtsReviewServiceImpl(this, this, workItemService);
      workDefCache = new AtsWorkDefinitionCache();

      attributeResolverService = new AtsAttributeResolverServiceImpl();
      relationResolver = new AtsRelationResolverServiceImpl(this);
      attributeResolverService.setOrcsApi(orcsApi);
      atsCache = new AtsCache(this);
      workDefService.setWorkDefinitionStringProvider(this);
      workDefAdmin = new AtsWorkDefinitionAdminImpl(workDefCache, workDefService, attributeResolverService,
         teamWorkflowProvidersLazy);

      atsLogFactory = AtsCoreFactory.newLogFactory();
      atsStateFactory = AtsCoreFactory.newStateFactory(getServices(), atsLogFactory);
      atsStoreService = new AtsStoreServiceImpl(attributeResolverService, this, atsStateFactory, atsLogFactory, this);

      utilService = AtsCoreFactory.getUtilService(attributeResolverService);
      sequenceProvider = new ISequenceProvider() {

         @Override
         public long getNext(String sequenceName) {
            // Sequence is set to sequential
            return jdbcClient.getNextSequence(sequenceName, false);
         }

      };
      atsQueryService = new AtsQueryServiceImpl(this, jdbcService);
      actionableItemManager = new ActionableItemManager(attributeResolverService, atsStoreService, this);
      actionFactory = new ActionFactory(workItemFactory, utilService, sequenceProvider, actionableItemManager,
         attributeResolverService, atsStateFactory, getServices());
      atsProgramService = new AtsProgramService(this);
      atsTeamDefinitionService = new AtsTeamDefinitionService(this);

      agileService = new AgileService(logger, this);
      versionService = new AtsVersionServiceImpl(getServices());
      taskService = new AtsTaskService(this);
      earnedValueService = new AtsEarnedValueImpl(logger, this);

      addAtsDatabaseConversion(new ConvertBaselineGuidToBaselineUuid(logger, jdbcClient, orcsApi, this));
      addAtsDatabaseConversion(new ConvertFavoriteBranchGuidToUuid(logger, jdbcClient, orcsApi, this));

      logger.info("ATS Application started");
   }

   public void stop() {
      jdbcClient = null;
   }

   @Override
   public OrcsApi getOrcsApi() throws OseeCoreException {
      return orcsApi;
   }

   @Override
   public IAtsWorkItemFactory getWorkItemFactory() throws OseeCoreException {
      return workItemFactory;
   }

   @Override
   public IAtsWorkDefinitionService getWorkDefService() throws OseeCoreException {
      return workDefService;
   }

   @Override
   public IAtsUserService getUserService() throws OseeCoreException {
      return userService;
   }

   @Override
   public ArtifactReadable getArtifact(ArtifactId artifact) throws OseeCoreException {
      ArtifactReadable result = null;
      if (artifact instanceof ArtifactReadable) {
         result = (ArtifactReadable) artifact;
      } else if (artifact instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) artifact;
         if (atsObject.getStoreObject() != null) {
            result = (ArtifactReadable) atsObject.getStoreObject();
         } else {
            result = query.fromBranch(getAtsBranch()).andUuid(atsObject.getId()).getResults().getAtMostOneOrNull();
         }
      } else {
         result = query.fromBranch(getAtsBranch()).andId(artifact).getResults().getOneOrNull();
      }
      return result;
   }

   @Override
   public ArtifactReadable getArtifact(IAtsObject atsObject) throws OseeCoreException {
      ArtifactReadable result = null;
      if (atsObject.getStoreObject() != null) {
         result = (ArtifactReadable) atsObject.getStoreObject();
      } else {
         result = query.fromBranch(getAtsBranch()).andUuid(atsObject.getId()).getResults().getAtMostOneOrNull();
      }
      return result;
   }

   @Override
   public IAtsWorkItemService getWorkItemService() throws OseeStateException {
      return workItemService;
   }

   @Override
   public IAtsBranchService getBranchService() {
      return branchService;
   }

   @Override
   public IAtsReviewService getReviewService() {
      return reviewService;
   }

   @Override
   public ArtifactReadable getArtifactByGuid(String guid) throws OseeCoreException {
      ArtifactReadable artifact = null;
      try {
         artifact = query.fromBranch(getAtsBranch()).andGuid(guid).getResults().getExactlyOne();
      } catch (ItemDoesNotExist ex) {
         // do nothing
      }
      return artifact;
   }

   @Override
   public ArtifactReadable getArtifactByAtsId(String id) {
      ArtifactReadable artifact = null;
      try {
         artifact = query.fromBranch(getAtsBranch()).and(AtsAttributeTypes.AtsId, id).getResults().getOneOrNull();
      } catch (ItemDoesNotExist ex) {
         // do nothing
      }
      return artifact;
   }

   @Override
   public IAtsWorkDefinitionAdmin getWorkDefAdmin() {
      return workDefAdmin;
   }

   @Override
   public IAttributeResolver getAttributeResolver() {
      return attributeResolverService;
   }

   @Override
   public IAtsStoreService getStoreService() {
      return atsStoreService;
   }

   @Override
   public IAtsLogFactory getLogFactory() {
      return atsLogFactory;
   }

   @Override
   public IAtsStateFactory getStateFactory() {
      return atsStateFactory;
   }

   @Override
   public Iterable<IAtsDatabaseConversion> getDatabaseConversions() {
      return externalConversions.values();
   }

   @Override
   public IAtsUtilService getUtilService() {
      return utilService;
   }

   @Override
   public ISequenceProvider getSequenceProvider() {
      return sequenceProvider;
   }

   @Override
   public IAtsActionFactory getActionFactory() {
      return actionFactory;
   }

   @Override
   public ArtifactReadable getArtifactById(String id) {
      ArtifactReadable action = null;
      if (GUID.isValid(id)) {
         action = getArtifactByGuid(id);
      }
      Long uuid = null;
      try {
         uuid = Long.parseLong(id);
      } catch (NumberFormatException ex) {
         // do nothing
      }
      if (uuid != null) {
         action = getArtifact(uuid);
      }
      if (action == null) {
         action = getArtifactByAtsId(id);
      }
      return action;
   }

   @Override
   public ArtifactReadable getArtifact(Long uuid) {
      return query.fromBranch(getAtsBranch()).andUuid(uuid).getResults().getOneOrNull();
   }

   @Override
   public Collection<ArtifactReadable> getArtifacts(List<Long> uuids) {
      Collection<ArtifactReadable> artifacts = new LinkedList<>();
      Iterator<ArtifactReadable> iterator = query.fromBranch(getAtsBranch()).andUuids(uuids).getResults().iterator();
      while (iterator.hasNext()) {
         artifacts.add(iterator.next());
      }
      return artifacts;
   }

   @Override
   public List<ArtifactReadable> getArtifactListByIds(String ids) {
      List<ArtifactReadable> actions = new ArrayList<>();
      for (String id : ids.split(",")) {
         id = id.replaceAll("^ +", "");
         id = id.replaceAll(" +$", "");
         ArtifactReadable action = getArtifactById(id);
         if (action != null) {
            actions.add(action);
         }
      }
      return actions;
   }

   @Override
   public QueryBuilder getQuery() {
      return getOrcsApi().getQueryFactory().fromBranch(getAtsBranch());
   }

   @Override
   public String getConfigValue(String key) {
      String result = null;
      ArtifactReadable atsConfig =
         query.fromBranch(COMMON).andIds(AtsArtifactToken.AtsConfig).getResults().getAtMostOneOrNull();
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
   public boolean isProduction() {
      return jdbcClient.getConfig().isProduction();
   }

   @Override
   public IAtsServices getServices() {
      return this;
   }

   @Override
   public void sendNotifications(AtsNotificationCollector notifications) {
      if (isEmailEnabled()) {
         if (notifiers.isEmpty() || !isProduction()) {
            if (!loggedNotificationDisabled) {
               logger.info("Osee Notification Disabled");
               loggedNotificationDisabled = true;
            }
         } else {
            workItemNotificationProcessor =
               new WorkItemNotificationProcessor(logger, this, workItemFactory, userService, attributeResolverService);
            notificationEventProcessor = new AtsNotificationEventProcessor(workItemNotificationProcessor, userService,
               getConfigValue("NoReplyEmail"));
            notificationEventProcessor.sendNotifications(notifications, notifiers);
         }
      }
   }

   @Override
   public IAtsVersionService getVersionService() {
      return versionService;
   }

   public AtsNotifierServiceImpl getNotifyService() {
      return notifyService;
   }

   @Override
   public List<IAtsWorkItem> getWorkItemListByIds(String ids) {
      List<IAtsWorkItem> workItems = new ArrayList<>();
      for (ArtifactReadable art : getArtifactListByIds(ids)) {
         IAtsWorkItem workItem = workItemFactory.getWorkItem(art);
         if (workItem != null) {
            workItems.add(workItem);
         }
      }
      return workItems;
   }

   @Override
   public void setChangeType(IAtsObject atsObject, ChangeType changeType, IAtsChangeSet changes) {
      ChangeTypeUtil.setChangeType(atsObject, changeType, changes);
   }

   @Override
   public ChangeType getChangeType(IAtsAction fromAction) {
      return ChangeTypeUtil.getChangeType(fromAction);
   }

   @Override
   public Collection<IArtifactType> getArtifactTypes() {
      List<IArtifactType> types = new ArrayList<>();
      types.addAll(orcsApi.getOrcsTypes().getArtifactTypes().getAll());
      return types;
   }

   @Override
   public IRelationResolver getRelationResolver() {
      return relationResolver;
   }

   public boolean isEmailEnabled() {
      return emailEnabled;
   }

   @Override
   public void setEmailEnabled(boolean emailEnabled) {
      this.emailEnabled = emailEnabled;
   }

   @Override
   public IAtsProgramService getProgramService() {
      return atsProgramService;
   }

   @Override
   public IAtsTeamDefinitionService getTeamDefinitionService() {
      return atsTeamDefinitionService;
   }

   @Override
   public IAgileService getAgileService() {
      return agileService;
   }

   @Override
   public IAtsQueryService getQueryService() {
      return atsQueryService;
   }

   @Override
   public <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz) throws OseeCoreException {
      return getCache().getAtsObject(uuid);
   }

   @Override
   public Collection<ITransitionListener> getTransitionListeners() {
      return Collections.emptyList();
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      // do nothing; no cache on server
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
   public ArtifactId getArtifactByName(IArtifactType artifactType, String name) {
      return query.fromBranch(getAtsBranch()).andIsOfType(artifactType).andNameEquals(
         name).getResults().getAtMostOneOrNull();
   }

   @Override
   public IAtsEarnedValueService getEarnedValueService() {
      return earnedValueService;
   }

   @Override
   public AtsConfigurations getConfigurations() {
      if (configurationsProvider == null) {
         configurationsProvider = new AtsConfigEndpointImpl(this, orcsApi, logger);
      }
      return configurationsProvider.get();
   }

   @Override
   public void clearConfigurationsCaches() {
      // do nothing
   }

   @Override
   public IAtsEarnedValueServiceProvider getEarnedValueServiceProvider() {
      return this;
   }

   @Override
   public IAtsColumnService getColumnService() {
      if (columnServices == null) {
         columnServices = AtsCoreFactory.getColumnService(getServices());
      }
      return columnServices;
   }

   @Override
   public CustomizeData getCustomizationByGuid(String customize_guid) {
      CustomizeData cust = null;
      ArtifactReadable customizeStoreArt = query.fromBranch(getAtsBranch()).and(CoreAttributeTypes.XViewerCustomization,
         customize_guid, QueryOption.CONTAINS_MATCH_OPTIONS).getResults().getAtMostOneOrNull();
      if (customizeStoreArt != null) {
         for (String custXml : getAttributeResolver().getAttributesToStringList(customizeStoreArt,
            CoreAttributeTypes.XViewerCustomization)) {
            if (custXml.contains(customize_guid)) {
               cust = new CustomizeData(custXml);
               break;
            }
         }
      }
      return cust;
   }

   private List<ArtifactId> getCustomizeArts() {
      List<ArtifactId> customizationArts = getGlobalCustomizeArts();
      for (ArtifactId artifact : getQuery().andIsOfType(CoreArtifactTypes.User).getResults()) {
         customizationArts.add(artifact);
      }
      return customizationArts;
   }

   private List<ArtifactId> getGlobalCustomizeArts() {
      List<ArtifactId> customizationArts = new ArrayList<>();
      for (ArtifactId artifact : query.fromBranch(CoreBranches.COMMON).andIsOfType(
         CoreArtifactTypes.XViewerGlobalCustomization).getResults()) {
         customizationArts.add(artifact);
      }
      return customizationArts;
   }

   @Override
   public Collection<CustomizeData> getCustomizations(String namespace) {
      List<CustomizeData> customizations = new ArrayList<>();
      for (ArtifactId customizationArt : getCustomizeArts()) {
         addCustomizationsFromArts(namespace, customizations, customizationArt);
      }
      return customizations;
   }

   private void addCustomizationsFromArts(String namespace, List<CustomizeData> customizations, ArtifactId customizationArt) {
      for (String custXml : getAttributeResolver().getAttributesToStringList(customizationArt,
         CoreAttributeTypes.XViewerCustomization)) {
         if (custXml.contains("\"" + namespace + "\"")) {
            CustomizeData data = new CustomizeData(custXml);
            customizations.add(data);
         }
      }
   }

   @Override
   public Collection<CustomizeData> getCustomizationsGlobal(String namespace) {
      List<CustomizeData> customizations = new ArrayList<>();
      for (ArtifactId customizationArt : getGlobalCustomizeArts()) {
         addCustomizationsFromArts(namespace, customizations, customizationArt);
      }
      return customizations;
   }

   @Override
   public IAtsImplementerService getImplementerService() {
      if (implementerService == null) {
         implementerService = new AtsImplementersService(this);
      }
      return implementerService;
   }

   @Override
   public IAtsWorkDefinitionAdmin getWorkDefinitionAdmin() {
      return workDefAdmin;
   }

   @Override
   public IAtsCache getCache() {
      return atsCache;
   }

   @Override
   public Log getLogger() {
      return logger;
   }

   @Override
   public IAtsChangeSet createChangeSet(String comment, IAtsUser user) {
      return getStoreService().createAtsChangeSet(comment, user);
   }

   @Override
   public IAtsChangeSet createChangeSet(String comment) {
      return getStoreService().createAtsChangeSet(comment, userService.getCurrentUser());
   }

   @Override
   public boolean isNotificationsEnabled() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setNotifactionsEnabled(boolean enabled) {
      throw new UnsupportedOperationException();
   }
}