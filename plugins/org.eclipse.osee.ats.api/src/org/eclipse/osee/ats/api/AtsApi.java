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

package org.eclipse.osee.ats.api;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.access.IAtsAccessService;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprintHtmlOperation;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
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
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsHealthService;
import org.eclipse.osee.ats.api.util.IAtsServerEndpointProvider;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsActionService;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsDatabaseTypeProvider;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.framework.core.OseeApi;
import org.eclipse.osee.framework.core.access.IAccessControlService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public interface AtsApi extends OseeApi, IAtsEarnedValueServiceProvider, IAtsWorkItemServiceProvider {

   BranchToken getAtsBranch();

   IRelationResolver getRelationResolver();

   IAttributeResolver getAttributeResolver();

   IAtsUserService getUserService();

   IAtsReviewService getReviewService();

   IAtsBranchService getBranchService();

   IAtsWorkDefinitionService getWorkDefinitionService();

   IAtsVersionService getVersionService();

   String getAtsId(ArtifactToken artifact);

   String getAtsId(IAtsObject atsObject);

   Collection<ArtifactTypeToken> getArtifactTypes();

   IAtsStoreService getStoreService();

   void clearImplementersCache(IAtsWorkItem workItem);

   IArtifactResolver getArtifactResolver();

   IAtsTaskService getTaskService();

   IAtsProgramService getProgramService();

   IAtsQueryService getQueryService();

   @Override
   IAtsEarnedValueService getEarnedValueService();

   IAtsEarnedValueServiceProvider getEarnedValueServiceProvider();

   IAtsImplementerService getImplementerService();

   IAtsColumnService getColumnService();

   ISequenceProvider getSequenceProvider();

   IAtsActionService getActionService();

   /**
    * @param key - key of key/value config pair. equals sign not accepted
    */
   String getConfigValue(String key);

   String getConfigValue(String key, String defaultValue);

   Log getLogger();

   void setConfigValue(String key, String value);

   IAtsChangeSet createChangeSet(String comment);

   IAtsChangeSet createChangeSet(String comment, AtsUser user);

   void storeAtsBranch(BranchId branch, String name);

   List<IAtsSearchDataProvider> getSearchDataProviders();

   void clearCaches();

   ITeamWorkflowProvidersLazy getTeamWorkflowProviders();

   IAtsStateFactory getStateFactory();

   IAtsWorkStateFactory getWorkStateFactory();

   IAtsLogFactory getLogFactory();

   IAtsTeamDefinitionService getTeamDefinitionService();

   IAgileService getAgileService();

   JdbcService getJdbcService();

   String getApplicationServerBase();

   Collection<IAgileSprintHtmlOperation> getAgileSprintHtmlReportOperations();

   public default String getConfigValue(AtsConfigKey configKey, String defaultValue) {
      return getConfigValue(configKey.name(), defaultValue);
   }

   IAtsActionableItemService getActionableItemService();

   boolean isSingleServerDeployment();

   IAtsConfigurationsService getConfigService();

   IAtsTaskRelatedService getTaskRelatedService();

   IAtsHealthService getHealthService();

   IAtsNotificationService getNotificationService();

   IAtsWorkDefinitionProviderService getWorkDefinitionProviderService();

   /**
    * @return current Ats User Config string for key or null
    */
   String getUserConfigValue(String key);

   /**
    * Store current key/value in Ats User Config attribute
    */
   void setUserConfigValue(String key, String value);

   EventAdmin getEventAdmin();

   IAtsEventService getEventService();

   IAtsTaskSetDefinitionProviderService getTaskSetDefinitionProviderService();

   IAtsChangeSet createChangeSet(String comment, BranchId branch);

   IAtsChangeSet createChangeSet(String comment, BranchId branch, AtsUser asUser);

   IAtsServerEndpointProvider getServerEndpoints();

   IAtsAccessService getAtsAccessService();

   @Override
   IAccessControlService getAccessControlService();

   default void reloadServerAndClientCaches() {
      getServerEndpoints().getConfigEndpoint().getWithPend();
      getConfigService().getConfigurationsWithPend();
   }

   boolean isIde();

   default ArtifactId getStoreObject(IAtsObject atsObject) {
      if (atsObject.getStoreObject() != null) {
         if (!(atsObject instanceof AtsUser)) {
            return atsObject.getStoreObject();
         }
      }
      ArtifactToken obj = getQueryService().getArtifact(atsObject);
      atsObject.setStoreObject(obj);
      return obj;
   }

   UserService getUserGroupService();

   default long getRandomNum() {
      return Lib.generateId();
   }

   /**
    * @return value in static id field that starts with key=; key= will be stripped off string and remaining returned
    */
   default public String getStaticIdValue(IAtsWorkItem workItem, String key, String defaultValue) {
      return getAttributeResolver().getStaticIdValue(workItem, key, defaultValue);
   }

   /**
    * @return set/update static id in format of key=value
    */
   default public void setStaticIdValue(IAtsWorkItem workItem, String key, String value, IAtsChangeSet changes) {
      getAttributeResolver().setStaticIdValue(workItem, key, value, changes);
   }

   List<IAtsDatabaseTypeProvider> getDatabaseTypeProviders();

}