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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemService {

   IArtifactType getArtifactType(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<Object> getAttributeValues(IAtsObject workItem, IAttributeType attributeType) throws OseeCoreException;

   boolean isOfType(IAtsWorkItem item, IArtifactType matchType) throws OseeCoreException;

   IAtsTeamWorkflow getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException;

   int getTransactionNumber(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<IAtsTeamWorkflow> getTeams(IAtsAction action) throws OseeCoreException;

   IStateToken getCurrentState(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf, IStateToken relatedToState) throws OseeCoreException;

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state) throws OseeCoreException;

   Collection<IAtsTask> getTasks(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IAtsTeamWorkflow getFirstTeam(IAtsAction action) throws OseeCoreException;

   String getCurrentStateName(IAtsWorkItem workItem);

   void clearImplementersCache(IAtsWorkItem workItem);

   Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef) throws OseeStateException;

   Collection<IAtsTask> getTaskArtifacts(IAtsWorkItem workItem) throws OseeCoreException;

   Collection<ITransitionListener> getTransitionListeners();

   String getTargetedVersionStr(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   String getTeamName(IAtsTeamWorkflow teamWf);

   Collection<? extends IAtsTask> getTasks(IAtsWorkItem workItem, IStateToken state);

   String getArtifactTypeShortName(IAtsTeamWorkflow teamWf);

}
