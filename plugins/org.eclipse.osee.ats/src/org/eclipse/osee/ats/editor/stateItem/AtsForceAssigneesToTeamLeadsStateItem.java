/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.stateItem;

import java.util.Collection;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.transition.ITransitionListener;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsStateItem extends AtsStateItem implements ITransitionListener {

   public AtsForceAssigneesToTeamLeadsStateItem() {
      super(AtsForceAssigneesToTeamLeadsStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "Check if toState is configured to force assignees to leads and set leads accordingly.";
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      if (sma.isTeamWorkflow() && isForceAssigneesToTeamLeads(sma.getStateDefinitionByName(toState.getPageName()))) {
         Collection<IBasicUser> teamLeads = ((TeamWorkFlowArtifact) sma).getTeamDefinition().getLeads();
         if (!teamLeads.isEmpty()) {
            sma.getStateMgr().setAssignees(teamLeads);
            sma.persist(transaction);
         }
      }
   }

   private boolean isForceAssigneesToTeamLeads(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.ForceAssigneesToTeamLeads);
   }

   @Override
   public void transitioning(TransitionResults results, AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<? extends IBasicUser> toAssignees) {
      // do nothing
   }

}
