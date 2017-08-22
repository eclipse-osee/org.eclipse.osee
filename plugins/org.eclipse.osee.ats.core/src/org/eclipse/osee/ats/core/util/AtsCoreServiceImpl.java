/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.config.IAtsConfigurationProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionDslService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsActionFactory;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.config.AtsCache;
import org.eclipse.osee.ats.core.config.TeamDefinitionService;
import org.eclipse.osee.ats.core.program.AtsProgramService;
import org.eclipse.osee.ats.core.review.AtsReviewServiceImpl;
import org.eclipse.osee.ats.core.version.AtsVersionServiceImpl;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionStoreService;
import org.eclipse.osee.ats.core.workflow.AtsImplementersService;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsCoreServiceImpl implements IAtsServices {

   private static final Object lock = new Object();
   private volatile static IOseeBranch atsBranch;
   private static final String ATS_BRANCH_NAME = "ats.branch.name";
   private static final String ATS_BRANCH_UUID = "ats.branch.uuid";

   private final List<IAtsSearchDataProvider> searchDataProviders;

   protected Log logger;
   protected IAtsCache atsCache;
   protected JdbcService jdbcService;

   protected AtsWorkDefinitionStoreService workDefinitionStore;
   protected IAtsWorkDefinitionService workDefinitionService;
   protected IAtsWorkDefinitionDslService workDefinitionDslService;
   protected IAtsUserService userService;
   protected IAtsConfigurationProvider configProvider;
   protected IAtsEarnedValueService earnedValueService;
   protected TeamWorkflowProviders teamWorkflowProvidersLazy;
   protected IAttributeResolver attributeResolverService;
   protected IAtsActionFactory actionFactory;
   protected IAtsImplementerService implementerService;
   protected IAtsWorkItemService workItemService;
   protected ISequenceProvider sequenceProvider;
   protected IAtsProgramService programService;
   protected IAtsStateFactory stateFactory;
   protected IArtifactResolver artifactResolver;
   protected IVersionFactory versionFactory;
   protected IAtsBranchService branchService;
   protected IAtsReviewService reviewService;
   protected IAtsWorkStateFactory workStateFactory;
   protected IAtsLogFactory logFactory;
   protected IAtsColumnService columnServices;
   protected IAtsWorkItemFactory workItemFactory;
   protected IAtsConfigItemFactory configItemFactory;
   protected IAtsActionableItemService actionableItemManager;
   protected IRelationResolver relationResolver;
   protected IAtsVersionService versionService;
   protected IAtsTaskService taskService;
   protected IAtsTeamDefinitionService teamDefinitionService;
   protected IAtsQueryService queryService;
   protected IAtsStoreService storeService;

   private EventAdmin eventAdmin;

   public AtsCoreServiceImpl() {
      searchDataProviders = new ArrayList<>();
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

   public void setConfigurationsService(IAtsConfigurationProvider configProvider) {
      this.configProvider = configProvider;
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

   public void setWorkDefinitionDslService(IAtsWorkDefinitionDslService workDefinitionDslService) {
      this.workDefinitionDslService = workDefinitionDslService;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(workDefinitionDslService, "IAtsWorkDefinitionService");

      atsCache = new AtsCache(this);
      teamWorkflowProvidersLazy = new TeamWorkflowProviders();
      workItemService = new AtsWorkItemServiceImpl(this, teamWorkflowProvidersLazy);

      workDefinitionStore = new AtsWorkDefinitionStoreService(this);
      programService = new AtsProgramService(this);
      teamDefinitionService = new TeamDefinitionService(this);
      versionService = new AtsVersionServiceImpl(this, eventAdmin);
      reviewService = new AtsReviewServiceImpl(this);
   }

   public void stop() {
      if (workDefinitionService != null) {
         workDefinitionService.clearCaches();
      }
      workDefinitionService = null;
      atsCache = null;
      jdbcService = null;
      versionFactory = null;
   }

   @Override
   public void clearCaches() {
      atsCache.invalidate();
      workDefinitionService.clearCaches();
   }

   @Override
   public String getAtsId(ArtifactId artifact) {
      return getAtsId(getAttributeResolver(), artifact);
   }

   @Override
   public String getAtsId(IAtsObject atsObject) {
      return getAtsId(getAttributeResolver(), atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, IAtsObject atsObject) {
      return getAtsId(attrResolver, atsObject.getStoreObject());
   }

   protected static String getAtsId(IAttributeResolver attrResolver, ArtifactId artifact) {
      Conditions.checkNotNull(artifact, "artifact");
      String toReturn = attrResolver.getSoleAttributeValue(artifact, AtsAttributeTypes.AtsId, null);
      if (toReturn == null) {
         toReturn = artifact.getGuid();
      }
      return toReturn;
   }

   @Override
   public <T> T getConfigItem(ArtifactId artifact) {
      return getConfigItem(artifact.getId());
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(String id) {
      T atsObject = null;
      if (GUID.isValid(id)) {
         atsObject = getCache().getAtsObjectByGuid(id);
         if (atsObject == null) {
            ArtifactId artifact = getArtifactByGuid(id);
            if (artifact != null && artifact instanceof IAtsConfigObject) {
               atsObject = (T) getConfigItemFactory().getConfigObject(artifact);
            }
         }
      } else if (Strings.isNumeric(id)) {
         atsObject = getCache().getAtsObject(Long.valueOf(id));
         if (atsObject == null) {
            ArtifactId artifact = getArtifact(Long.valueOf(id));
            if (artifact != null && artifact instanceof IAtsConfigObject) {
               atsObject = (T) getConfigItemFactory().getConfigObject(artifact);
            }
         }
      }
      return atsObject;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getConfigItem(Long uuid) {
      T atsObject = getCache().getAtsObject(uuid);
      if (atsObject == null) {
         ArtifactId artifact = getArtifact(uuid);
         if (artifact != null && artifact instanceof IAtsConfigObject) {
            atsObject = (T) getConfigItemFactory().getConfigObject(artifact);
         }
      }
      return atsObject;
   }

   @Override
   public void setConfigValue(String key, String value) {
      ArtifactId atsConfig = getArtifact(AtsArtifactToken.AtsConfig);
      IAtsChangeSet changes =
         getStoreService().createAtsChangeSet("Set AtsConfig Value", getUserService().getCurrentUser());
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
   public IOseeBranch getAtsBranch() {
      synchronized (lock) {
         if (atsBranch == null) {
            // Preference store overrides all
            if (AtsPreferencesService.isAvailable()) {
               try {
                  String atsBranchUuid = AtsPreferencesService.get(ATS_BRANCH_UUID);
                  setConfig(atsBranchUuid, AtsPreferencesService.get(ATS_BRANCH_NAME));
               } catch (Exception ex) {
                  OseeLog.log(AtsUtilCore.class, Level.SEVERE, "Error processing stored ATS Branch.", ex);
               }
            }
            // osee.ini -D option overrides default
            if (atsBranch == null) {
               String atsBranchUuid = System.getProperty(ATS_BRANCH_UUID);
               if (Strings.isValid(atsBranchUuid)) {
                  setConfig(atsBranchUuid, System.getProperty(ATS_BRANCH_NAME));
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

   private void setConfig(String branchUuid, String name) {
      if (!Strings.isValid(name)) {
         name = "unknown";
      }
      if (Strings.isValid(branchUuid) && branchUuid.matches("\\d+")) {
         atsBranch = IOseeBranch.create(Long.valueOf(branchUuid), name);
      }
   }

   @Override
   public void storeAtsBranch(BranchId branch, String name) {
      AtsPreferencesService.get().put(ATS_BRANCH_UUID, branch.getIdString());
      AtsPreferencesService.get().put(ATS_BRANCH_NAME, name);
   }

   @Override
   public IAtsCache getCache() {
      return atsCache;
   }

   @Override
   public List<IAtsSearchDataProvider> getSearchDataProviders() {
      return searchDataProviders;
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
   public IAtsUserService getUserService() throws OseeStateException {
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
   public AtsConfigurations getConfigurations() {
      return configProvider.getConfigurations();
   }

   @Override
   public ITeamWorkflowProvidersLazy getTeamWorkflowProviders() {
      return teamWorkflowProvidersLazy;
   }

   @Override
   public IAtsActionFactory getActionFactory() {
      return actionFactory;
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
   public IAtsReviewService getReviewService() throws OseeCoreException {
      return reviewService;
   }

   @Override
   public IAtsStateFactory getStateFactory() {
      if (stateFactory == null) {
         stateFactory = AtsCoreFactory.newStateFactory(this, getLogFactory());
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
   public IArtifactResolver getArtifactResolver() {
      return artifactResolver;
   }

   @Override
   public IAtsWorkItemFactory getWorkItemFactory() {
      return workItemFactory;
   }

   @Override
   public IAtsConfigItemFactory getConfigItemFactory() {
      return configItemFactory;
   }

   @Override
   public IRelationResolver getRelationResolver() {
      return relationResolver;
   }

   @Override
   public IAtsBranchService getBranchService() throws OseeCoreException {
      return branchService;
   }

   @Override
   public IVersionFactory getVersionFactory() {
      return versionFactory;
   }

   @Override
   public IAtsColumnService getColumnService() {
      if (columnServices == null) {
         columnServices = AtsCoreFactory.getColumnService(this);
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
   public IAtsChangeSet createChangeSet(String comment, IAtsUser user) {
      return getStoreService().createAtsChangeSet(comment, user);
   }

   // Quick Search for ATS Id takes 22 seconds, just use straight database call instead.  Replace this when searching is improved.
   private static String ATS_ID_QUERY =
      "SELECT art.art_id FROM osee_artifact art, osee_txs txs, OSEE_ATTRIBUTE attr WHERE art.gamma_id = txs.gamma_id " //
         + "AND txs.tx_current = 1 AND txs.branch_id = ? and attr.ART_ID = art.ART_ID and " //
         + "attr.ATTR_TYPE_ID = 1152921504606847877 and attr.VALUE = ?";

   @Override
   public ArtifactToken getArtifactByAtsId(String id) {
      ArtifactToken artifact = null;
      try {
         Collection<ArtifactToken> workItems =
            getQueryService().getArtifactsFromQuery(ATS_ID_QUERY, getAtsBranch(), id);
         if (!workItems.isEmpty()) {
            artifact = workItems.iterator().next();
         }
      } catch (ItemDoesNotExist ex) {
         // do nothing
      }
      return artifact;
   }

   @Override
   public IAtsTeamWorkflow getTeamWf(ArtifactToken artifact) {
      ArtifactId art = getArtifact(artifact);
      if (art != null) {
         return getWorkItemFactory().getTeamWf(art);
      }
      return null;
   }

}
