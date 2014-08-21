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

import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.util.IAtsStoreFactory;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsSequenceProvider;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionAdminImpl;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.action.IAtsActionFactory;
import org.eclipse.osee.ats.impl.action.IWorkItemPage;
import org.eclipse.osee.ats.impl.internal.action.ActionFactory;
import org.eclipse.osee.ats.impl.internal.convert.AtsDatabaseConversions;
import org.eclipse.osee.ats.impl.internal.util.AtsArtifactConfigCache;
import org.eclipse.osee.ats.impl.internal.util.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsBranchServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsNotificationServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsReviewServiceImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsStoreFactoryImpl;
import org.eclipse.osee.ats.impl.internal.util.AtsUtilServer;
import org.eclipse.osee.ats.impl.internal.util.AtsWorkDefinitionCacheProvider;
import org.eclipse.osee.ats.impl.internal.util.TeamWorkflowProvider;
import org.eclipse.osee.ats.impl.internal.workitem.ActionUtility;
import org.eclipse.osee.ats.impl.internal.workitem.ActionableItemManager;
import org.eclipse.osee.ats.impl.internal.workitem.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.impl.internal.workitem.ConfigItemFactory;
import org.eclipse.osee.ats.impl.internal.workitem.WorkItemFactory;
import org.eclipse.osee.ats.impl.internal.workitem.WorkItemPage;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class AtsServerImpl implements IAtsServer {

   public static String PLUGIN_ID = "org.eclipse.osee.ats.rest";
   private static OrcsApi orcsApi;
   private Log logger;
   private static IAtsWorkItemFactory workItemFactory;
   private static AtsServerImpl instance;
   private static IAtsWorkDefinitionService workDefService;
   private static IAtsUserService userService;
   private static IAtsNotificationService notifyService;
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
   private IWorkItemPage workItemPage;
   private IAtsUtilService utilService;
   private AtsSequenceProvider sequenceProvider;
   private IAtsActionFactory actionFactory;
   private ActionableItemManager actionableItemManager;
   private ActionUtility actionUtil;
   private IOseeDatabaseService dbService;

   public static AtsServerImpl get() {
      checkStarted();
      return instance;
   }

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
      AtsServerImpl.orcsApi = orcsApi;
   }

   public void setWorkDefService(IAtsWorkDefinitionService workDefService) {
      AtsServerImpl.workDefService = workDefService;
   }

   public void setUserService(IAtsUserService userService) {
      AtsServerImpl.userService = userService;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(orcsApi, "OrcsApi");
      Conditions.checkNotNull(workDefService, "IAtsWorkDefinitionService");
      Conditions.checkNotNull(userService, "IAtsUserService");
      instance = this;
      workItemFactory = new WorkItemFactory(logger, this);
      configItemFactory = new ConfigItemFactory(logger, this);
      notifyService = new AtsNotificationServiceImpl();

      workItemService = new AtsWorkItemServiceImpl(this, workItemFactory, this);
      branchService = new AtsBranchServiceImpl(orcsApi);
      reviewService = new AtsReviewServiceImpl(this, workItemService);
      workDefCacheProvider = new AtsWorkDefinitionCacheProvider(workDefService);

      teamWorkflowProvider = new TeamWorkflowProvider();
      attributeResolverService = new AtsAttributeResolverServiceImpl();
      attributeResolverService.setOrcsApi(orcsApi);
      workDefAdmin =
         new AtsWorkDefinitionAdminImpl(workDefCacheProvider, workItemService, workDefService, teamWorkflowProvider,
            attributeResolverService);

      atsLogFactory = AtsCoreFactory.newLogFactory();
      atsStateFactory = AtsCoreFactory.newStateFactory(attributeResolverService, userService, notifyService);
      atsStoreFactory = new AtsStoreFactoryImpl(this);

      utilService = AtsCoreFactory.getUtilService(attributeResolverService);
      sequenceProvider = new AtsSequenceProvider(dbService);
      config = new AtsArtifactConfigCache(this, orcsApi);
      actionableItemManager = new ActionableItemManager(config);
      actionUtil = new ActionUtility(orcsApi);
      workItemPage = new WorkItemPage(logger, this);
      actionFactory =
         new ActionFactory(orcsApi, workItemFactory, utilService, sequenceProvider, workItemService,
            actionableItemManager, userService, attributeResolverService, atsStateFactory);

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
      checkStarted();
      return userService;
   }

   @Override
   public ArtifactReadable getArtifact(IAtsObject atsObject) throws OseeCoreException {
      checkStarted();
      return AtsUtilServer.getArtifact(orcsApi, atsObject);
   }

   @Override
   public IAtsNotificationService getNotifyService() throws OseeCoreException {
      checkStarted();
      return notifyService;
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
      return AtsUtilServer.getArtifactByGuid(orcsApi, guid);
   }

   @Override
   public ArtifactReadable getArtifactByAtsId(String id) {
      return AtsUtilServer.getArtifactByAtsId(orcsApi, id);
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
   public IWorkItemPage getWorkItemPage() {
      return workItemPage;
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
   public ArtifactReadable getActionById(String id) {
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

}
