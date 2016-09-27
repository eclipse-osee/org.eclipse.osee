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
package org.eclipse.osee.ats.core.client;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsEventService;
import org.eclipse.osee.ats.api.version.IAtsVersionServiceProvider;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.api.workdef.IAttributeResolverProvider;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.core.client.config.IAtsClientVersionService;
import org.eclipse.osee.ats.core.client.internal.IArtifactProvider;
import org.eclipse.osee.ats.core.client.internal.IAtsWorkItemArtifactServiceProvider;
import org.eclipse.osee.ats.core.client.util.IArtifactMembersCache;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.IAtsCacheProvider;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsClient extends IAtsServices, IAtsNotifier, IAttributeResolverProvider, IAtsReviewServiceProvider, IAtsBranchServiceProvider, IAtsWorkItemArtifactServiceProvider, IAtsWorkItemServiceProvider, IAtsCacheProvider, ITeamDefinitionFactory, IActionableItemFactory, IArtifactProvider, IAtsVersionServiceProvider, IAtsEarnedValueServiceProvider {

   Artifact getConfigArtifact(IAtsConfigObject atsConfigObject) throws OseeCoreException;

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   Artifact getArtifact(IArtifactToken token) throws OseeCoreException;

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   Artifact getArtifact(IAtsObject atsObject) throws OseeCoreException;

   AbstractWorkflowArtifact getWorkflowArtifact(IAtsObject atsObject) throws OseeCoreException;

   List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects) throws OseeCoreException;

   void invalidateCache();

   void reloadWorkDefinitionCache(boolean pend) throws OseeCoreException;

   void invalidateWorkDefinitionCache();

   void reloadAllCaches(boolean pend) throws OseeCoreException;

   void invalidateAllCaches();

   IAtsUserServiceClient getUserServiceClient();

   IAtsStateFactory getStateFactory();

   IAtsWorkStateFactory getWorkStateFactory();

   IAtsLogFactory getLogFactory();

   IAtsServices getServices();

   IAtsTeamDefinitionService getTeamDefinitionService();

   @Override
   Artifact getArtifact(Long uuid) throws OseeCoreException;

   @Override
   IAtsClientVersionService getVersionService();

   @Override
   IAtsWorkItemFactory getWorkItemFactory();

   @Override
   IAtsConfigItemFactory getConfigItemFactory();

   IVersionFactory getVersionFactory();

   @Override
   Artifact getArtifactByGuid(String guid) throws OseeCoreException;

   Artifact getArtifactByAtsId(String id);

   IArtifactMembersCache<GoalArtifact> getGoalMembersCache();

   IArtifactMembersCache<SprintArtifact> getSprintItemsCache();

   Artifact checkArtifactFromId(long uuid, BranchId atsBranch);

   TeamWorkflowProviders getTeamWorkflowProviders();

   List<IAtsSearchDataProvider> getSearchDataProviders();

   IAtsEventService getEventService();

   void reloadConfigCache(boolean pend);

   void reloadUserCache(boolean pend);

   IAtsChangeSet createAtsChangeSet(String comment);

}
