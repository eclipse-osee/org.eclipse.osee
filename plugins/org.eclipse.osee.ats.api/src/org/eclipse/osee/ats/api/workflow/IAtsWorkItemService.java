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
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemService {

   int getTransactionNumber(IAtsWorkItem workItem) throws OseeCoreException;

   IStateToken getCurrentState(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf, IStateToken relatedToState) throws OseeCoreException;

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state) throws OseeCoreException;

   Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IAtsTeamWorkflow getFirstTeam(IAtsAction action) throws OseeCoreException;

   void clearImplementersCache(IAtsWorkItem workItem);

   Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeStateException;

   Collection<IAtsTask> getTaskArtifacts(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<ITransitionListener> getTransitionListeners();

   String getTargetedVersionStr(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   Collection<? extends IAtsTask> getTasks(IAtsWorkItem workItem, IStateToken state);

   String getArtifactTypeShortName(IAtsTeamWorkflow teamWf);

}
