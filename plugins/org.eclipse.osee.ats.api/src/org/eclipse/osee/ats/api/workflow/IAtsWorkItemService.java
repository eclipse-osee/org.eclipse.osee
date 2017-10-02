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
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemService {

   IStateToken getCurrentState(IAtsWorkItem workItem) ;

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) ;

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state) ;

   IAtsTeamWorkflow getFirstTeam(Object object) ;

   void clearImplementersCache(IAtsWorkItem workItem);

   Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef) ;

   Collection<ITransitionListener> getTransitionListeners();

   String getTargetedVersionStr(IAtsTeamWorkflow teamWf) ;

   String getArtifactTypeShortName(IAtsTeamWorkflow teamWf);

   Collection<IAtsTeamWorkflow> getTeams(Object object);

   IAtsActionableItemService getActionableItemService();

   /**
    * Assigned or Combined Id that will show at the top of the editor. Default is "<ATS Id> / <Legacy PCR Id (if set)>"
    */
   String getCombinedPcrId(IAtsWorkItem workItem) ;

   IAtsWorkItemNotes getNotes(IAtsWorkItem workItem);

   ITeamWorkflowProvidersLazy getTeamWorkflowProviders();

   IAtsWorkItem getWorkItemByAnyId(String actionId);

}
