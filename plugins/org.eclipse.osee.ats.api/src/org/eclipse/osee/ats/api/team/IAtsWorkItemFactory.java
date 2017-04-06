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
package org.eclipse.osee.ats.api.team;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G Dunne
 */
public interface IAtsWorkItemFactory {

   IAtsTeamWorkflow getTeamWf(ArtifactToken artifact);

   IAtsWorkItem getWorkItem(ArtifactToken artifact);

   IAtsTask getTask(ArtifactToken artifact);

   IAtsAbstractReview getReview(ArtifactToken artifact);

   IAtsGoal getGoal(ArtifactToken artifact);

   IAtsAction getAction(ArtifactToken artifact);

   IAtsWorkItem getWorkItemByAtsId(String atsId);

   IAgileSprint getAgileSprint(ArtifactToken artifact);

   IAgileBacklog getAgileBacklog(ArtifactToken artifact);

   IAgileItem getAgileItem(ArtifactToken artifact);

   Collection<IAtsWorkItem> getWorkItems(Collection<? extends ArtifactToken> artifacts);

   IAtsTeamWorkflow getTeamWfNoCache(ArtifactId artifact);

   IAtsTeamWorkflow getTeamWf(ArtifactId artifact);

}
