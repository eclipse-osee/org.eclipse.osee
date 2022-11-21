/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsOperationCache {

   IAtsTeamWorkflow getParentTeamWorkflow(IAtsWorkItem workItem, HealthCheckResults results);

   List<ArtifactToken> getTeamDefinitions();

   List<ArtifactToken> getActionableItems();

   void addTeamWf(ArtifactToken teamWf);

   void addTask(IAtsTask workItem);

   Map<Long, IAtsTask> getTasks();

   Map<Long, IAtsAbstractReview> getReviews();

   void addReview(IAtsAbstractReview review);

}