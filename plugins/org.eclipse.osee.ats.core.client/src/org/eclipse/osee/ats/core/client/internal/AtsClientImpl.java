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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProviders;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.IAtsUserServiceClient;
import org.eclipse.osee.ats.core.client.IAtsVersionAdmin;
import org.eclipse.osee.ats.core.client.internal.config.ActionableItemFactory;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.client.internal.config.AtsConfigCacheProvider;
import org.eclipse.osee.ats.core.client.internal.config.TeamDefinitionFactory;
import org.eclipse.osee.ats.core.client.internal.config.VersionFactory;
import org.eclipse.osee.ats.core.client.internal.ev.AtsEarnedValueImpl;
import org.eclipse.osee.ats.core.client.internal.notify.AtsNotificationServiceImpl;
import org.eclipse.osee.ats.core.client.internal.query.AtsQuery;
import org.eclipse.osee.ats.core.client.internal.review.AtsReviewServiceImpl;
import org.eclipse.osee.ats.core.client.internal.store.ActionableItemArtifactReader;
import org.eclipse.osee.ats.core.client.internal.store.ActionableItemArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.store.AtsArtifactStore;
import org.eclipse.osee.ats.core.client.internal.store.AtsVersionCache;
import org.eclipse.osee.ats.core.client.internal.store.AtsVersionServiceImpl;
import org.eclipse.osee.ats.core.client.internal.store.TeamDefinitionArtifactReader;
import org.eclipse.osee.ats.core.client.internal.store.TeamDefinitionArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.store.VersionArtifactReader;
import org.eclipse.osee.ats.core.client.internal.store.VersionArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.workdef.AtsWorkDefinitionCacheProvider;
import org.eclipse.osee.ats.core.client.internal.workdef.AtsWorkItemArtifactProviderImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsAttributeResolverServiceImpl;
import org.eclipse.osee.ats.core.client.internal.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AtsBranchServiceImpl;
import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.IVersionFactory;
import org.eclipse.osee.ats.core.util.AtsCoreFactory;
import org.eclipse.osee.ats.core.util.AtsSequenceProvider;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionAdminImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionCache;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsClientImpl implements IAtsClient {

   private IAtsStateFactory stateFactory;
   private IAtsWorkDefinitionService workDefService;
   private IAtsWorkItemArtifactService workItemArtifactProvider;
   private final AtsConfigProxy configProxy = new AtsConfigProxy();
   private IAtsVersionAdmin versionService;
   private IAtsArtifactStore artifactStore;
   private CacheProvider<AtsArtifactConfigCache> configCacheProvider;
   private IAtsWorkDefinitionAdmin workDefAdmin;
   private IActionableItemFactory actionableItemFactory;
   private ITeamDefinitionFactory teamDefFactory;
   private IVersionFactory versionFactory;
   private CacheProvider<AtsWorkDefinitionCache> workDefCacheProvider;
   private IAtsEarnedValueService earnedValueService;
   private IAtsUserService userService;
   private IAtsUserServiceClient userServiceClient;
   private IAtsWorkItemService workItemService;
   private IAtsBranchService branchService;
   private IAtsReviewService reviewService;
   private IAttributeResolver attributeResolverService;
   private ITeamWorkflowProviders teamWorkflowProvider;
   private ISequenceProvider sequenceProvider;
   private IAtsWorkStateFactory workStateFactory;
   private IAtsLogFactory logFactory;
   private IAtsNotificationService notificationService;
   private IAtsColumnUtilities columnUtilities;
   private IAtsUtilService utilService;
   private IOseeDatabaseService dbService;

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setAtsWorkDefinitionService(IAtsWorkDefinitionService workDefService) {
      this.workDefService = workDefService;
   }

   public void setAtsUserService(IAtsUserService atsUserService) {
      this.userService = atsUserService;
      this.userServiceClient = (IAtsUserServiceClient) userService;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(workDefService, "IAtsWorkDefinitionService");
      Conditions.checkNotNull(userService, "IAtsUserService");
      Map<Class<? extends IAtsConfigObject>, IAtsArtifactWriter<? extends IAtsConfigObject>> writers =
         new HashMap<Class<? extends IAtsConfigObject>, IAtsArtifactWriter<? extends IAtsConfigObject>>();

      Map<IArtifactType, IAtsArtifactReader<? extends IAtsConfigObject>> readers =
         new HashMap<IArtifactType, IAtsArtifactReader<? extends IAtsConfigObject>>();

      writers.put(IAtsActionableItem.class, new ActionableItemArtifactWriter());
      writers.put(IAtsTeamDefinition.class, new TeamDefinitionArtifactWriter());
      writers.put(IAtsVersion.class, new VersionArtifactWriter());

      artifactStore = new AtsArtifactStore(readers, writers);
      configCacheProvider = new AtsConfigCacheProvider(artifactStore);
      earnedValueService = new AtsEarnedValueImpl();

      AtsVersionCache versionCache = new AtsVersionCache(configCacheProvider);
      versionService = new AtsVersionServiceImpl(configCacheProvider, artifactStore, versionCache);

      actionableItemFactory = new ActionableItemFactory();
      teamDefFactory = new TeamDefinitionFactory();
      versionFactory = new VersionFactory(versionService);

      readers.put(AtsArtifactTypes.ActionableItem, new ActionableItemArtifactReader(actionableItemFactory,
         teamDefFactory, versionFactory, userServiceClient));
      readers.put(AtsArtifactTypes.TeamDefinition, new TeamDefinitionArtifactReader(actionableItemFactory,
         teamDefFactory, versionFactory, versionService, userServiceClient));
      readers.put(AtsArtifactTypes.Version, new VersionArtifactReader(actionableItemFactory, teamDefFactory,
         versionFactory, versionService));

      teamWorkflowProvider = TeamWorkFlowManager.getTeamWorkflowProviders();

      workDefCacheProvider = new AtsWorkDefinitionCacheProvider(workDefService);
      workItemArtifactProvider = new AtsWorkItemArtifactProviderImpl();
      workItemService = new AtsWorkItemServiceImpl(workItemArtifactProvider);
      attributeResolverService = new AtsAttributeResolverServiceImpl();

      workDefAdmin =
         new AtsWorkDefinitionAdminImpl(workDefCacheProvider, workItemService, workDefService, teamWorkflowProvider,
            attributeResolverService);
      branchService = new AtsBranchServiceImpl();
      reviewService = new AtsReviewServiceImpl(this);
   }

   public void stop() {
      if (workDefAdmin != null) {
         workDefAdmin.clearCaches();
      }
      workDefAdmin = null;

      if (configCacheProvider != null) {
         configCacheProvider.invalidate();
         configCacheProvider = null;
      }

      if (workDefCacheProvider != null) {
         workDefCacheProvider.invalidate();
         workDefCacheProvider = null;
      }
      versionService = null;
      artifactStore = null;
      actionableItemFactory = null;
      teamDefFactory = null;
      versionFactory = null;

   }

   @Override
   public <T extends IAtsConfigObject> Artifact storeConfigObject(T configObject, IAtsChangeSet changes) throws OseeCoreException {
      AtsArtifactConfigCache atsConfigCache = getConfigCache();
      return artifactStore.store(atsConfigCache, configObject, changes);
   }

   @Override
   public <T extends IAtsConfigObject> T getConfigObject(Artifact artifact) throws OseeCoreException {
      AtsArtifactConfigCache atsConfigCache = getConfigCache();
      return artifactStore.load(atsConfigCache, artifact);
   }

   @Override
   public Artifact getConfigArtifact(IAtsConfigObject atsConfigObject) throws OseeCoreException {
      return getConfigCache().getArtifact(atsConfigObject);
   }

   @Override
   public List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects) throws OseeCoreException {
      return getConfigCache().getArtifacts(atsObjects);
   }

   @Override
   public <T extends IAtsConfigObject> Collection<T> getConfigObjects(Collection<? extends Artifact> artifacts, Class<T> clazz) throws OseeCoreException {
      IAtsConfig config = getConfig();
      List<T> objects = new ArrayList<T>();
      for (Artifact art : artifacts) {
         objects.addAll(config.getByTag(art.getGuid(), clazz));
      }
      return objects;
   }

   @Override
   public void invalidateConfigCache() {
      configCacheProvider.invalidate();
   }

   @Override
   public void reloadConfigCache() throws OseeCoreException {
      configCacheProvider.invalidate();
      configCacheProvider.get();
   }

   @Override
   public void reloadWorkDefinitionCache() throws OseeCoreException {
      workDefCacheProvider.invalidate();
      workDefCacheProvider.get();
   }

   @Override
   public void reloadAllCaches() throws OseeCoreException {
      reloadConfigCache();
      reloadWorkDefinitionCache();
      getUserService().clearCache();
   }

   @Override
   public void invalidateAllCaches() {
      invalidateConfigCache();
      invalidateWorkDefinitionCache();
      versionService.invalidateVersionCache();
   }

   @Override
   public void invalidateWorkDefinitionCache() {
      workDefCacheProvider.invalidate();
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String guid, String title) throws OseeCoreException {
      IAtsTeamDefinition item = teamDefFactory.createTeamDefinition(guid, title);
      AtsArtifactConfigCache cache = getConfigCache();
      cache.cache(item);
      return item;
   }

   @Override
   public IAtsActionableItem createActionableItem(String guid, String name) throws OseeCoreException {
      IAtsActionableItem item = actionableItemFactory.createActionableItem(guid, name);
      AtsArtifactConfigCache cache = getConfigCache();
      cache.cache(item);
      return item;
   }

   @Override
   public IAtsVersion createVersion(String title, String guid) throws OseeCoreException {
      IAtsVersion item = versionFactory.createVersion(title, guid);
      AtsArtifactConfigCache cache = getConfigCache();
      cache.cache(item);
      return item;
   }

   @Override
   public IAtsVersion createVersion(String name) throws OseeCoreException {
      IAtsVersion item = versionFactory.createVersion(name);
      AtsArtifactConfigCache cache = getConfigCache();
      cache.cache(item);
      return item;
   }

   @Override
   public IAtsWorkDefinitionAdmin getWorkDefinitionAdmin() throws OseeStateException {
      return workDefAdmin;
   }

   @Override
   public IAtsConfig getConfig() throws OseeStateException {
      return configProxy;
   }

   @Override
   public IAtsVersionAdmin getVersionService() throws OseeStateException {
      return versionService;
   }

   @Override
   public IAtsUserServiceClient getUserServiceClient() throws OseeStateException {
      return userServiceClient;
   }

   @Override
   public IAtsUserService getUserService() throws OseeStateException {
      return userService;
   }

   @Override
   public IAtsQuery createQuery(Collection<? extends IAtsWorkItem> workItems) {
      return new AtsQuery(workItems, workItemService, workItemArtifactProvider);
   }

   private AtsArtifactConfigCache getConfigCache() throws OseeCoreException {
      return configCacheProvider.get();
   }

   private final class AtsConfigProxy implements IAtsConfig {

      @Override
      public <A extends IAtsConfigObject> List<A> getByTag(String tag, Class<A> clazz) throws OseeCoreException {
         return getConfigCache().getByTag(tag, clazz);
      }

      @Override
      public <A extends IAtsConfigObject> A getSoleByTag(String tag, Class<A> clazz) throws OseeCoreException {
         return getConfigCache().getSoleByTag(tag, clazz);
      }

      @Override
      public <A extends IAtsConfigObject> List<A> get(Class<A> clazz) throws OseeCoreException {
         return getConfigCache().get(clazz);
      }

      @Override
      public <A extends IAtsConfigObject> A getSoleByGuid(String guid, Class<A> clazz) throws OseeCoreException {
         return getConfigCache().getSoleByGuid(guid, clazz);
      }

      @Override
      public IAtsConfigObject getSoleByGuid(String guid) throws OseeCoreException {
         return getConfigCache().getSoleByGuid(guid);
      }

      @Override
      public void getReport(XResultData rd) throws OseeCoreException {
         getConfigCache().getReport(rd);
      }

      @Override
      public void invalidate(IAtsConfigObject configObject) throws OseeCoreException {
         getConfigCache().invalidate(configObject);
      }

   }

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   public Artifact getArtifact(IAtsObject atsObject) throws OseeCoreException {
      Artifact results = null;
      if (atsObject.getStoreObject() != null) {
         results = (Artifact) atsObject.getStoreObject();
      } else {
         if (atsObject instanceof Artifact) {
            results = (Artifact) atsObject;
         } else {
            try {
               results = AtsArtifactQuery.getArtifactFromId(atsObject.getGuid());
            } catch (ArtifactDoesNotExist ex) {
               // do nothing
            }
         }
      }
      return results;
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
   public IAtsWorkItemArtifactService getWorkItemArtifactService() throws OseeStateException {
      return workItemArtifactProvider;
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
      if (sequenceProvider == null) {
         sequenceProvider = new AtsSequenceProvider(dbService);
      }
      return sequenceProvider;
   }

   @Override
   public IAtsStateFactory getStateFactory() {
      if (stateFactory == null) {
         stateFactory = AtsCoreFactory.newStateFactory(getAttributeResolver(), getUserService(), getNotifyService());
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
   public IAtsNotificationService getNotifyService() {
      if (notificationService == null) {
         notificationService = new AtsNotificationServiceImpl();
      }
      return notificationService;
   }

   @Override
   public IAtsColumnUtilities getColumnUtilities() {
      if (columnUtilities == null) {
         columnUtilities = AtsCoreFactory.getColumnUtilities(getReviewService(), getWorkItemService());
      }
      return columnUtilities;
   }

   @Override
   public IAtsUtilService getUtilService() {
      if (utilService == null) {
         utilService = AtsCoreFactory.getUtilService(getAttributeResolver());
      }
      return utilService;
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

}
