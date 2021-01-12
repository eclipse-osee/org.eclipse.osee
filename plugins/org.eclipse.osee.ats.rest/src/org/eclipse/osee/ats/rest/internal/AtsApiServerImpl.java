/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.task.related.IAtsTaskRelatedService;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.util.IAtsHealthService;
import org.eclipse.osee.ats.api.util.IAtsServerEndpointProvider;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.core.agile.AgileService;
import org.eclipse.osee.ats.core.ai.ActionableItemServiceImpl;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsApiImpl;
import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.ats.rest.internal.config.AtsConfigurationsService;
import org.eclipse.osee.ats.rest.internal.config.ConvertWorkDefinitionsToJava;
import org.eclipse.osee.ats.rest.internal.convert.ConvertBaselineGuidToBaselineId;
import org.eclipse.osee.ats.rest.internal.convert.ConvertFavoriteBranchGuidToId;
import org.eclipse.osee.ats.rest.internal.health.AtsHealthServiceImpl;
import org.eclipse.osee.ats.rest.internal.notify.AtsNotificationEventProcessor;
import org.eclipse.osee.ats.rest.internal.notify.AtsNotifierServiceImpl;
import org.eclipse.osee.ats.rest.internal.notify.WorkItemNotificationProcessor;
import org.eclipse.osee.ats.rest.internal.query.AtsQueryServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.ArtifactResolverImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsBranchServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsEarnedValueImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsRelationResolverServiceImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsStoreServiceImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsActionEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTaskService;
import org.eclipse.osee.ats.rest.util.IAtsNotifierServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupService;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G Dunne
 */
public class AtsApiServerImpl extends AtsApiImpl implements AtsApiServer {

   public static String PLUGIN_ID = "org.eclipse.osee.ats.rest";
   private OrcsApi orcsApi;
   private AtsNotifierServiceImpl notifyService;
   private AtsNotificationEventProcessor notificationEventProcessor;
   private IAgileService agileService;
   private volatile boolean emailEnabled = true;
   private boolean loggedNotificationDisabled = false;
   private final List<IAtsNotifierServer> notifiers = new CopyOnWriteArrayList<>();
   private final Map<String, IAtsDatabaseConversion> externalConversions = new ConcurrentHashMap<>();
   private AtsActionEndpointApi actionEndpoint;
   private IAtsHealthService healthService;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
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

   // for ReviewOsgiXml public void setAtsEventService(IAtsEventService eventService)
   // for ReviewOsgiXml public void setJdbcService(JdbcService jdbcService)
   // for ReviewOsgiXml public void setEventAdmin(EventAdmin eventAdmin)
   // for ReviewOsgiXml public void setLogger(Log logger)
   // for ReviewOsgiXml public void setOrcsTokenService(OrcsTokenService tokenService)
   // for ReviewOsgiXml public void setAtsUserService(IAtsUserService userServiceClient)
   // for ReviewOsgiXml public void setWorkDefinitionProviderService(IAtsWorkDefinitionProviderService workDefinitionProviderService)
   // for ReviewOsgiXml public void setTaskSetDefinitionProviderService(IAtsTaskSetDefinitionProviderService taskSetDefinitionProviderService) {

   @Override
   public void start() {
      configurationsService = new AtsConfigurationsService(this, orcsApi);
      attributeResolverService = new AtsAttributeResolverServiceImpl();

      super.start();

      notifyService = new AtsNotifierServiceImpl();

      artifactResolver = new ArtifactResolverImpl(this);
      branchService = new AtsBranchServiceImpl(this, orcsApi, teamWorkflowProvidersLazy);

      relationResolver = new AtsRelationResolverServiceImpl(this);
      ((AtsAttributeResolverServiceImpl) attributeResolverService).setOrcsApi(orcsApi);
      ((AtsAttributeResolverServiceImpl) attributeResolverService).setServices(this);

      storeService = new AtsStoreServiceImpl(this, orcsApi, stateFactory, logFactory, this);

      queryService = new AtsQueryServiceImpl(this, jdbcService, orcsApi);
      actionableItemManager = new ActionableItemServiceImpl(attributeResolverService, this);
      actionFactory = new ActionFactory(attributeResolverService, this);

      agileService = new AgileService(logger, this);
      taskService = new AtsTaskService(this);
      earnedValueService = new AtsEarnedValueImpl(logger, this);

      addAtsDatabaseConversion(new ConvertBaselineGuidToBaselineId(logger, jdbcService.getClient(), orcsApi, this));
      addAtsDatabaseConversion(new ConvertFavoriteBranchGuidToId(logger, jdbcService.getClient(), orcsApi, this));
      addAtsDatabaseConversion(new ConvertWorkDefinitionsToJava());

      loadAtsConfigCache();

      logger.info("ATS Application started");
   }

   private void loadAtsConfigCache() {
      Thread loadCache = new Thread("Load ATS Config Cache") {

         @Override
         public void run() {
            super.run();
            getConfigService().getConfigurationsWithPend();
         }

      };
      loadCache.start();
   }

   @Override
   public void stop() {
      super.stop();
   }

   @Override
   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public Iterable<IAtsDatabaseConversion> getDatabaseConversions() {
      return externalConversions.values();
   }

   @Override
   public String getConfigValue(String key) {
      String result = null;
      ArtifactToken atsConfig = getQueryService().getArtifact(AtsArtifactToken.AtsConfig);
      if (atsConfig != null) {
         Collection<String> attributeValues =
            getAttributeResolver().getAttributesToStringList(atsConfig, CoreAttributeTypes.GeneralStringData);
         for (String str : attributeValues) {
            if (str.startsWith(key)) {
               result = str.replaceFirst(key + "=", "");
               break;
            }
         }
      }
      return result;
   }

   @Override
   public void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String body) {
      if (isEmailEnabled()) {
         Thread thread = new Thread("ATS Emailer") {

            @Override
            public void run() {
               for (IAtsNotifierServer notifier : notifiers) {
                  notifier.sendNotifications(fromUserEmail, toUserEmails, subject, body);
               }
            }

         };
         thread.start();
      }
   }

   @Override
   public void sendNotifications(AtsNotificationCollector notifications) {
      if (isEmailEnabled()) {
         if (notifiers.isEmpty() || !getStoreService().isProductionDb()) {
            if (!loggedNotificationDisabled) {
               logger.info("Osee Notification Disabled");
               loggedNotificationDisabled = true;
            }
         } else {
            if (notifications.isIncludeCancelHyperlink() && !getWorkItemService().isCancelHyperlinkConfigured()) {
               throw new OseeArgumentException("Cancel Hyperlink URl not configured");
            }
            WorkItemNotificationProcessor workItemNotificationProcessor =
               new WorkItemNotificationProcessor(logger, this, userService, attributeResolverService);
            Thread thread = new Thread("ATS Notification Sender") {

               @Override
               public void run() {
                  super.run();
                  notificationEventProcessor = new AtsNotificationEventProcessor(workItemNotificationProcessor,
                     userService, getConfigValue("NoReplyEmail"));
                  notificationEventProcessor.sendNotifications(notifications, notifiers);
               }

            };
            thread.start();
         }
      }

   }

   public AtsNotifierServiceImpl getNotifyService() {
      return notifyService;
   }

   @Override
   public Collection<ArtifactTypeToken> getArtifactTypes() {
      List<ArtifactTypeToken> types = new ArrayList<>();
      types.addAll(orcsApi.tokenService().getArtifactTypes());
      return types;
   }

   public boolean isEmailEnabled() {
      return emailEnabled;
   }

   @Override
   public void setEmailEnabled(boolean emailEnabled) {
      this.emailEnabled = emailEnabled;
   }

   @Override
   public IAgileService getAgileService() {
      return agileService;
   }

   @Override
   public void clearImplementersCache(IAtsWorkItem workItem) {
      // do nothing; no cache on server
   }

   private List<ArtifactId> getCustomizeArts() {
      List<ArtifactId> customizationArts = getGlobalCustomizeArts();
      for (ArtifactId artifact : getQueryService().getArtifacts(CoreArtifactTypes.User)) {
         customizationArts.add(artifact);
      }
      return customizationArts;
   }

   private List<ArtifactId> getGlobalCustomizeArts() {
      List<ArtifactId> customizationArts = new ArrayList<>();
      for (ArtifactId artifact : getQueryService().getArtifacts(CoreArtifactTypes.XViewerGlobalCustomization)) {
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
         if (custXml.contains("\"" + namespace + "\"") || custXml.contains("." + namespace + "\"")) {
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
   public boolean isNotificationsEnabled() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setNotifactionsEnabled(boolean enabled) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clearCaches() {
      super.clearCaches();

      getConfigService().getConfigurationsWithPend();
   }

   @Override
   public AtsActionEndpointApi getActionEndpoint() {
      if (actionEndpoint == null) {
         actionEndpoint = new AtsActionEndpointImpl(this, orcsApi);
      }
      return actionEndpoint;
   }

   @Override
   public String getApplicationServerBase() {
      return System.getProperty("OseeApplicationServer");
   }

   @Override
   public IAtsActionableItemService getActionableItemService() {
      return actionableItemManager;
   }

   @Override
   public boolean isWorkDefAsName() {
      return "true".equals(OseeInfo.getCachedValue(getJdbcService().getClient(), "osee.work.def.as.name"));
   }

   @Override
   public IAtsTaskRelatedService getTaskRelatedService() {
      return taskRelatedService;
   }

   @Override
   public IAtsHealthService getHealthService() {
      if (healthService == null) {
         healthService = new AtsHealthServiceImpl();
      }
      return healthService;
   }

   @Override
   public IAtsServerEndpointProvider getServerEndpoints() {
      // Not supported on server cause you can use server services
      return null;
   }

   @Override
   public boolean isIde() {
      return false;
   }

   @Override
   public IUserGroupService getUserGroupService() {
      return orcsApi.getUserGroupService();
   }

}