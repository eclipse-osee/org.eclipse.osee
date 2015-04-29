/*******************************************************************************
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
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.workdef.WorkDefData;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.agile.AgileService;
import org.eclipse.osee.ats.core.ai.ActionableItemManager;
import org.eclipse.osee.ats.core.util.ActionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsCoreServiceImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionServiceImpl;
import org.eclipse.osee.ats.core.workflow.WorkItemFactory;
import org.eclipse.osee.ats.rest.IAtsServer;
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
import org.eclipse.osee.ats.rest.internal.util.AtsStoreServiceImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTaskService;
import org.eclipse.osee.ats.rest.internal.workitem.ConfigItemFactory;
import org.eclipse.osee.ats.rest.util.ChangeTypeUtil;
import org.eclipse.osee.ats.rest.util.IAtsNotifierServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G Dunne
 */
public class AtsServerImpl extends AtsCoreServiceImpl implements IAtsServer {

   public static String PLUGIN_ID = "org.eclipse.osee.ats.rest";
   private static IAtsServices services;
   private OrcsApi orcsApi;
   private AtsNotifierServiceImpl notifyService;
   private AtsNotificationEventProcessor notificationEventProcessor;
   private IAgileService agileService;
   private volatile boolean emailEnabled = true;
   private boolean loggedNotificationDisabled = false;

   private final List<IAtsNotifierServer> notifiers = new CopyOnWriteArrayList<>();
   private final Map<String, IAtsDatabaseConversion> externalConversions =
      new ConcurrentHashMap<String, IAtsDatabaseConversion>();
   private AtsConfigEndpointImpl configurationEndpoint;

   public AtsServerImpl() {
      super();
      services = this;
   }

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

   @Override
   public void start() throws OseeCoreException {
      attributeResolverService = new AtsAttributeResolverServiceImpl();

      super.start();

      // ATS Server loads it Work Definitions from the database
      workDefinitionService = new AtsWorkDefinitionServiceImpl(this, workDefinitionStore, workDefinitionStore,
         workDefinitionDslService, teamWorkflowProvidersLazy);

      notifyService = new AtsNotifierServiceImpl();
      workItemFactory = new WorkItemFactory(this);
      configItemFactory = new ConfigItemFactory(logger, this);

      artifactResolver = new ArtifactResolverImpl(this, orcsApi);
      branchService = new AtsBranchServiceImpl(getServices(), orcsApi, teamWorkflowProvidersLazy);

      relationResolver = new AtsRelationResolverServiceImpl(this);
      ((AtsAttributeResolverServiceImpl) attributeResolverService).setOrcsApi(orcsApi);
      ((AtsAttributeResolverServiceImpl) attributeResolverService).setServices(this);

      logFactory = AtsCoreFactory.newLogFactory();
      stateFactory = AtsCoreFactory.newStateFactory(getServices(), logFactory);
      storeService =
         new AtsStoreServiceImpl(attributeResolverService, this, stateFactory, logFactory, this, jdbcService);

      queryService = new AtsQueryServiceImpl(this, jdbcService);
      actionableItemManager = new ActionableItemManager(attributeResolverService, storeService, this);
      actionFactory = new ActionFactory(workItemFactory, sequenceProvider, actionableItemManager,
         attributeResolverService, stateFactory, getServices());

      agileService = new AgileService(logger, this);
      taskService = new AtsTaskService(this);
      earnedValueService = new AtsEarnedValueImpl(logger, this);

      addAtsDatabaseConversion(new ConvertBaselineGuidToBaselineUuid(logger, jdbcService.getClient(), orcsApi, this));
      addAtsDatabaseConversion(new ConvertFavoriteBranchGuidToUuid(logger, jdbcService.getClient(), orcsApi, this));

      logger.info("ATS Application started");
   }

   @Override
   public void stop() {
      super.stop();
   }

   @Override
   public OrcsApi getOrcsApi() throws OseeCoreException {
      return orcsApi;
   }

   @Override
   public ArtifactReadable getArtifact(ArtifactId artifact) throws OseeCoreException {
      ArtifactReadable result = null;
      if (artifact instanceof ArtifactReadable) {
         result = (ArtifactReadable) artifact;
      } else if (artifact instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) artifact;
         if (atsObject.getStoreObject() instanceof ArtifactReadable) {
            result = (ArtifactReadable) atsObject.getStoreObject();
         } else {
            result = orcsApi.getQueryFactory().fromBranch(getAtsBranch()).andUuid(
               atsObject.getId()).getResults().getAtMostOneOrNull();
         }
      } else {
         result = orcsApi.getQueryFactory().fromBranch(getAtsBranch()).andId(artifact).getResults().getOneOrNull();
      }
      return result;
   }

   @Override
   public ArtifactReadable getArtifact(IAtsObject atsObject) throws OseeCoreException {
      ArtifactReadable result = null;
      if (atsObject.getStoreObject() instanceof ArtifactReadable) {
         result = (ArtifactReadable) atsObject.getStoreObject();
      } else {
         result = orcsApi.getQueryFactory().fromBranch(getAtsBranch()).andUuid(
            atsObject.getId()).getResults().getAtMostOneOrNull();
         if (result != null) {
            atsObject.setStoreObject(result);
         }
      }
      return result;
   }

   @Override
   public ArtifactReadable getArtifactByGuid(String guid) throws OseeCoreException {
      ArtifactReadable artifact = null;
      try {
         artifact = orcsApi.getQueryFactory().fromBranch(getAtsBranch()).andGuid(guid).getResults().getExactlyOne();
      } catch (ItemDoesNotExist ex) {
         // do nothing
      }
      return artifact;
   }

   @Override
   public Iterable<IAtsDatabaseConversion> getDatabaseConversions() {
      return externalConversions.values();
   }

   @Override
   public ArtifactReadable getArtifactByAtsId(String id) {
      return (ArtifactReadable) super.getArtifactByAtsId(id);
   }

   @Override
   public ArtifactReadable getArtifactByLegacyPcrId(String id) {
      return (ArtifactReadable) super.getArtifactByLegacyPcrId(id);
   }

   @Override
   public ArtifactReadable getArtifactById(String id) {
      return (ArtifactReadable) getQueryService().getArtifactById(id);
   }

   @Override
   public ArtifactReadable getArtifact(Long uuid) {
      return orcsApi.getQueryFactory().fromBranch(getAtsBranch()).andUuid(uuid).getResults().getOneOrNull();
   }

   @Override
   public Collection<ArtifactToken> getArtifacts(Collection<Long> uuids) {
      Collection<ArtifactToken> artifacts = new LinkedList<>();
      Iterator<ArtifactReadable> iterator =
         orcsApi.getQueryFactory().fromBranch(getAtsBranch()).andUuids(uuids).getResults().iterator();
      while (iterator.hasNext()) {
         artifacts.add(iterator.next());
      }
      return artifacts;
   }

   @Override
   public QueryBuilder getQuery() {
      return getOrcsApi().getQueryFactory().fromBranch(getAtsBranch());
   }

   @Override
   public String getConfigValue(String key) {
      String result = null;
      ArtifactReadable atsConfig = orcsApi.getQueryFactory().fromBranch(COMMON).andId(
         AtsArtifactToken.AtsConfig).getResults().getAtMostOneOrNull();
      if (atsConfig != null) {
         List<String> attributeValues = atsConfig.getAttributeValues(CoreAttributeTypes.GeneralStringData);
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
   public boolean isProduction() {
      return jdbcService.getClient().getConfig().isProduction();
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
            WorkItemNotificationProcessor workItemNotificationProcessor =
               new WorkItemNotificationProcessor(logger, this, workItemFactory, userService, attributeResolverService);
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
   public ArtifactToken getArtifactByName(IArtifactType artifactType, String name) {
      return orcsApi.getQueryFactory().fromBranch(getAtsBranch()).andIsOfType(artifactType).andNameEquals(
         name).getResults().getAtMostOneOrNull();
   }

   @Override
   public AtsConfigurations getConfigurations() {
      return getConfigurationEndpoint().get();
   }

   @Override
   public AtsConfigEndpointApi getConfigurationEndpoint() {
      if (configurationEndpoint == null) {
         configurationEndpoint = new AtsConfigEndpointImpl(this, orcsApi, logger);
      }
      return configurationEndpoint;
   }

   @Override
   public void clearConfigurationsCaches() {
      // do nothing
   }

   @Override
   public CustomizeData getCustomizationByGuid(String customize_guid) {
      CustomizeData cust = null;
      ArtifactReadable customizeStoreArt =
         orcsApi.getQueryFactory().fromBranch(getAtsBranch()).and(CoreAttributeTypes.XViewerCustomization,
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
      for (ArtifactId artifact : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
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

   @Override
   public List<WorkDefData> getWorkDefinitionsData() {
      return workDefinitionStore.getWorkDefinitionsData();
   }

   @Override
   public void clearCaches() {
      super.clearCaches();

      // clear client config cache (read from server)
      clearConfigurationsCaches();
   }

   @Override
   public String getApplicationServerBase() {
      return System.getProperty("OseeApplicationServer");
   }

   public static IAtsServices get() {
      return services;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactId artifact) {
      return getWorkItemFactory().getTeamWf(services.getArtifact(artifact));
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(Long id) {
      return getWorkItemFactory().getTeamWf(services.getArtifact(id));
   }

}