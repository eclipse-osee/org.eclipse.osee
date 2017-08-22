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
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.util.IAtsEventService;
import org.eclipse.osee.ats.api.version.IAtsVersionServiceProvider;
import org.eclipse.osee.ats.api.workdef.IAttributeResolverProvider;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.artifact.SprintArtifact;
import org.eclipse.osee.ats.core.client.util.IArtifactMembersCache;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.IAtsCacheProvider;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public interface IAtsClient extends IAtsServices, IAtsNotifier, IAttributeResolverProvider, IAtsReviewServiceProvider, IAtsBranchServiceProvider, IAtsCacheProvider, ITeamDefinitionFactory, IActionableItemFactory, IAtsVersionServiceProvider {

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   Artifact getArtifact(ArtifactId artifact);

   Artifact getConfigArtifact(IAtsConfigObject atsConfigObject);

   /**
    * @return corresponding Artifact or null if not found
    */
   @Override
   Artifact getArtifact(IAtsObject atsObject);

   AbstractWorkflowArtifact getWorkflowArtifact(IAtsObject atsObject);

   List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects);

   void reloadWorkDefinitionCache(boolean pend);

   void reloadAllCaches(boolean pend);

   IAtsUserServiceClient getUserServiceClient();

   IAtsServices getServices();

   @Override
   Artifact getArtifactByGuid(String guid);

   IArtifactMembersCache<GoalArtifact> getGoalMembersCache();

   IArtifactMembersCache<SprintArtifact> getSprintItemsCache();

   @Override
   Artifact getArtifact(Long uuid);

   IAtsEventService getEventService();

   void reloadConfigCache(boolean pend);

   void reloadUserCache(boolean pend);

   OseeClient getOseeClient();

   <T> T getConfigItem(ArtifactToken configToken);

   <T> Collection<T> getConfigItems(ArtifactToken... configTokens);

   Artifact getArtifact(ArtifactId artifact, BranchId branch);

}