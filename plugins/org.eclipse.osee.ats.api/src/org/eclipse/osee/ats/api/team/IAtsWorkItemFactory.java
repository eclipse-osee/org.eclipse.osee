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

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G Dunne
 */
public interface IAtsWorkItemFactory {

   IAtsTeamWorkflow getTeamWf(Object artifact) throws OseeCoreException;

   IAtsWorkItem getWorkItem(Object object) throws OseeCoreException;

   IAtsTask getTask(Object artifact) throws OseeCoreException;

   IAtsAbstractReview getReview(Object artifact) throws OseeCoreException;

   IAtsGoal getGoal(Object artifact) throws OseeCoreException;

   IAtsAction getAction(Object artifact);

   IAtsWorkItem getWorkItemByAtsId(String atsId);

   IAgileSprint getAgileSprint(Object artifact) throws OseeCoreException;

   IAgileBacklog getAgileBacklog(Object artifact);

   IAgileItem getAgileItem(Object art);

}
