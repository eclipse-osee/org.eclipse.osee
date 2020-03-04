/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.agile.IAgileSprintHtmlOperation;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigKey;
import org.eclipse.osee.ats.api.config.IAtsConfigurationsService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.event.IAtsEventService;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
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
import org.eclipse.osee.ats.api.workflow.IAtsActionFactory;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.osgi.service.event.EventAdmin;

/**
 * @author Donald G. Dunne
 */
public interface AtsApi extends IAtsEarnedValueServiceProvider, IAtsWorkItemServiceProvider {

   IOseeBranch getAtsBranch();

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

   IAtsActionFactory getActionFactory();

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

   void sendNotifications(AtsNotificationCollector notifications);

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

   boolean isWorkDefAsName();

   IAtsTaskRelatedService getTaskRelatedService();

   IAtsHealthService getHealthService();

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

   IAtsChangeSet createChangeSet(String comment, Branch branch);

   IAtsChangeSet createChangeSet(String comment, Branch branch, AtsUser asUser);

   IAtsServerEndpointProvider getServerEndpoints();

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

}
