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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G Dunne
 */
public interface IAtsWorkItemFactory {

   IAtsTeamWorkflow getTeamWf(ArtifactId artifact) throws OseeCoreException;

   IAtsWorkItem getWorkItem(ArtifactId artifact) throws OseeCoreException;

   IAtsTask getTask(ArtifactId artifact) throws OseeCoreException;

   IAtsAbstractReview getReview(ArtifactId artifact) throws OseeCoreException;

   IAtsGoal getGoal(ArtifactId artifact) throws OseeCoreException;

   IAtsAction getAction(ArtifactId artifact);

   IAtsWorkItem getWorkItemByAtsId(String atsId);

   IAgileSprint getAgileSprint(ArtifactToken artifact) throws OseeCoreException;

   IAgileBacklog getAgileBacklog(ArtifactToken artifact);

   IAgileItem getAgileItem(ArtifactId artifact);

   Collection<IAtsWorkItem> getWorkItems(Collection<? extends ArtifactId> artifacts);

   IAtsWorkItem getTeamWfNoCache(ArtifactId artifact);

}
