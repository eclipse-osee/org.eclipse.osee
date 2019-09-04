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
package org.eclipse.osee.ats.ide.util;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionServiceProvider;
import org.eclipse.osee.ats.api.workdef.IAttributeResolverProvider;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.ide.config.IAtsUserServiceClient;
import org.eclipse.osee.ats.ide.query.AtsQueryServiceClient;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public interface IAtsClient extends AtsApi, IAtsNotifier, IAttributeResolverProvider, IAtsReviewServiceProvider, IAtsBranchServiceProvider, ITeamDefinitionFactory, IActionableItemFactory, IAtsVersionServiceProvider {

   List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects);

   void reloadAllCaches(boolean pend);

   IAtsUserServiceClient getUserServiceClient();

   AtsApi getServices();

   IArtifactMembersCache<GoalArtifact> getGoalMembersCache();

   IArtifactMembersCache<SprintArtifact> getSprintItemsCache();

   void reloadConfigCache(boolean pend);

   void reloadUserCache(boolean pend);

   OseeClient getOseeClient();

   void reloadServerAndClientCaches();

   IAtsClientUtil getClientUtils();

   AtsQueryServiceClient getQueryServiceClient();

}