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
package org.eclipse.osee.ats.impl.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.util.IAtsStoreFactory;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.ai.ActionableItemManager;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsSequenceProvider;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.IAtsActionFactory;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionAdminImpl;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.internal.convert.AtsDatabaseConversions;
import org.eclipse.osee.ats.impl.internal.notify.AtsNotificationEventProcessor;
import org.eclipse.osee.ats.impl.internal.notify.AtsNotifierServiceImpl;
import org.eclipse.osee.ats.impl.internal.notify.IAtsNotifierServer;
import org.eclipse.osee.ats.impl.internal.notify.WorkItemNotificationProcessor;
import org.eclipse.osee.ats.impl.internal.user.AtsUserServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsArtifactConfigCache;
import org.eclipse.osee.ats.impl.internal.util.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsBranchServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsRelationResolverServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsReviewServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsStoreFactoryImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsWorkDefinitionCacheProvider;
import org.eclipse.osee.ats.impl.internal.util.TeamWorkflowProvider;
import org.eclipse.osee.ats.impl.internal.workitem.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.impl.internal.workitem.ChangeTypeUtil;
import org.eclipse.osee.ats.impl.internal.workitem.ConfigItemFactory;
import org.eclipse.osee.ats.impl.internal.workitem.WorkItemFactory;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G Dunne
 */
public class AtsServerImpl implements IAtsServer {

   public static String PLUGIN_ID = "org.eclipse.osee.ats.rest";
   private OrcsApi orcsApi;
   private Log logger;
   private IAtsWorkItemFactory workItemFactory;
   private IAtsWorkDefinitionService workDefService;
   private IAtsUserService userService;
   private AtsNotifierServiceImpl notifyService;
   private IAtsWorkItemService workItemService;
   private IAtsBranchService branchService;
   private IAtsReviewService reviewService;
   private IAtsWorkDefinitionAdmin workDefAdmin;
   private AtsWorkDefinitionCacheProvider workDefCacheProvider;
   private TeamWorkflowProvider teamWorkflowProvider;
   private AtsAttributeResolverServiceImpl attributeResolverService;
   private IAtsConfig config;
   private IAtsConfigItemFactory configItemFactory;
   private static Boolean started = null;
   private IAtsLogFactory atsLogFactory;
   private IAtsStateFactory atsStateFactory;
   private IAtsStoreFactory atsStoreFactory;
   private IAtsUtilService utilService;
   private AtsSequenceProvider sequenceProvider;
   private IAtsActionFactory actionFactory;
   private ActionableItemManager actionableItemManager;
   private IOseeDatabaseService dbService;
   private final List<IAtsNotifierServer> notifiers = new CopyOnWriteArrayList<IAtsNotifierServer>();
   private WorkItemNotificationProcessor workItemNotificationProcessor;
   private AtsNotificationEventProcessor notificationEventProcessor;
   private IAtsVersionService versionService;
   private IRelationResolver relationResolver;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   @Override
   public IAtsConfigItemFactory getConfigItemFactory() {
      return configItemFactory;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setWorkDefService(IAtsWorkDefinitionService workDefService) {
      this.workDefService = workDefService;
   }

   public void addNotifier(IAtsNotifierServer notifier) {
      notifiers.add(notifier);
   }

   public void removeNotifier(IAtsNotifierServer notifier) {
      notifiers.remove(notifier);
   }

   public void start() throws OseeCoreException {

      notifyService = new AtsNotifierServiceImpl();
      workItemFactory = new WorkItemFactory(logger, this);
      configItemFactory = new ConfigItemFactory(logger, this);

      workItemService = new AtsWorkItemServiceImpl(this, this);
      branchService = new AtsBranchServiceImpl(getServices(), orcsApi, dbService);
      reviewService = new AtsReviewServiceImpl(this, workItemService);
      workDefCacheProvider = new AtsWorkDefinitionCacheProvider(workDefService);

      teamWorkflowProvider = new TeamWorkflowProvider();
      attributeResolverService = new AtsAttributeResolverServiceImpl();
      relationResolver = new AtsRelationResolverServiceImpl();
      attributeResolverService.setOrcsApi(orcsApi);
      workDefAdmin =
         new AtsWorkDefinitionAdminImpl(workDefCacheProvider, workItemService, workDefService, teamWorkflowProvider,
            attributeResolverService);

      userService = new AtsUserServiceImpl();

      atsLogFactory = AtsCoreFactory.newLogFactory();
      atsStateFactory = AtsCoreFactory.newStateFactory(getServices(), atsLogFactory);
      atsStoreFactory = new AtsStoreFactoryImpl(orcsApi, atsStateFactory, atsLogFactory, this);

      utilService = AtsCoreFactory.getUtilService(attributeResolverService);
      sequenceProvider = new AtsSequenceProvider(dbService);
      config = new AtsArtifactConfigCache(configItemFactory, orcsApi);
      actionableItemManager = new ActionableItemManager(config, attributeResolverService);
      actionFactory =
         new ActionFactory(workItemFactory, utilService, sequenceProvider, workItemService, actionableItemManager,
            userService, attributeResolverService, atsStateFactory, config, getServices());

      System.out.println("ATS - AtsServerImpl started");
      started = true;
   }

   private static void checkStarted() throws OseeStateException {
      if (started == null) {
         throw new OseeStateException("AtsServer did not start");
      }
   }

   @Override
   public OrcsApi getOrcsApi() throws OseeCoreException {
      checkStarted();
      return orcsApi;
   }

   @Override
   public IAtsWorkItemFactory getWorkItemFactory() throws OseeCoreException {
      checkStarted();
      return workItemFactory;
   }

   @Override
   public IAtsWorkDefinitionService getWorkDefService() throws OseeCoreException {
      checkStarted();
      return workDefService;
   }

   @Override
   public IAtsUserService getUserService() throws OseeCoreException {
      return userService;
   }

   @Override
   public ArtifactReadable getArtifact(IAtsObject atsObject) throws OseeCoreException {
      checkStarted();
      ArtifactReadable result = null;
      if (atsObject.getStoreObject() != null) {
         result = (ArtifactReadable) atsObject.getStoreObject();
      } else {
         result =
            orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andGuid(atsObject.getGuid()).getResults().getAtMostOneOrNull();
      }
      return result;
   }

   @Override
   public IAtsWorkItemService getWorkItemService() throws OseeStateException {
      checkStarted();
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
      checkStarted();
      return orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).andGuid(guid).getResults().getExactlyOne();
   }

   @Override
   public ArtifactReadable getArtifactByAtsId(String id) {
      return orcsApi.getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch()).and(AtsAttributeTypes.AtsId, id).getResults().getOneOrNull();
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
   public IAtsConfig getConfig() throws OseeStateException {
      checkStarted();
      return config;
   }

   @Override
   public IAtsStoreFactory getStoreFactory() {
      return atsStoreFactory;
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
   public List<IAtsDatabaseConversion> getDatabaseConversions() {
      return AtsDatabaseConversions.getConversions(getOrcsApi(), getDatabaseService());
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   @Override
   public IAtsUtilService getUtilService() {
      return utilService;
   }

   @Override
   public AtsSequenceProvider getSequenceProvider() {
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
      if (action == null) {
         action = getArtifactByAtsId(id);
      }
      return action;
   }

   @Override
   public List<ArtifactReadable> getArtifactListByIds(String ids) {
      List<ArtifactReadable> actions = new ArrayList<ArtifactReadable>();
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
      return getOrcsApi().getQueryFactory(null).fromBranch(AtsUtilCore.getAtsBranch());
   }

   @Override
   public String getConfigValue(String key) {
      String result = null;
      @SuppressWarnings("unchecked")
      ArtifactReadable atsConfig =
         orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andIds(AtsArtifactToken.AtsConfig).getResults().getExactlyOne();
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
      return getDatabaseService().isProduction();
   }

   @Override
   public IAtsServices getServices() {
      return this;
   }

   @Override
   public void sendNotifications(AtsNotificationCollector notifications) {
      if (notifiers.isEmpty() || !isProduction()) {
         OseeLog.log(AtsServerImpl.class, Level.INFO, "Osee Notification Disabled");
      } else {
         workItemNotificationProcessor =
            new WorkItemNotificationProcessor(this, workItemFactory, userService, attributeResolverService);
         notificationEventProcessor =
            new AtsNotificationEventProcessor(workItemNotificationProcessor, userService,
               getConfigValue("NoReplyEmail"));
         notificationEventProcessor.sendNotifications(notifications, notifiers);
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
      List<IAtsWorkItem> workItems = new ArrayList<IAtsWorkItem>();
      for (ArtifactReadable art : getArtifactListByIds(ids)) {
         IAtsWorkItem workItem = workItemFactory.getWorkItem(art);
         if (workItem != null) {
            workItems.add(workItem);
         }
      }
      return workItems;
   }

   @Override
   public String getAtsId(Object obj) {
      ArtifactReadable art = null;
      if (obj instanceof ArtifactReadable) {
         art = (ArtifactReadable) obj;
      } else if (obj instanceof IAtsObject) {
         art = (ArtifactReadable) ((IAtsObject) obj).getStoreObject();
      }
      Conditions.checkNotNull(art, "artifact");
      String toReturn = art.getSoleAttributeAsString(AtsAttributeTypes.AtsId, AtsUtilCore.DEFAULT_ATS_ID_VALUE);
      if (AtsUtilCore.DEFAULT_ATS_ID_VALUE.equals(toReturn)) {
         toReturn = art.getGuid();
      }
      return toReturn;
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
   public String getAtsId(IAtsAction action) {
      return getAtsId(action);
   }

   @Override
   public Collection<IArtifactType> getArtifactTypes() {
      List<IArtifactType> types = new ArrayList<IArtifactType>();
      types.addAll(orcsApi.getOrcsTypes(null).getArtifactTypes().getAll());
      return types;
   }

   @Override
   public IRelationResolver getRelationResolver() {
      return relationResolver;
   }
}
