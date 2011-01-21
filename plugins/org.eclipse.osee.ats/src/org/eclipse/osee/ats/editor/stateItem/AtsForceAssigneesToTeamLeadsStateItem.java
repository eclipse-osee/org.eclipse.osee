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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;

/**
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsStateItem extends AtsStateItem {

   public AtsForceAssigneesToTeamLeadsStateItem() {
      super(AtsForceAssigneesToTeamLeadsStateItem.class.getSimpleName());
   }

   @Override
   public String getDescription() {
      return "Check if toState is configured to force assignees to leads and set leads accordingly.";
   }

   @Override
   public void transitioned(AbstractWorkflowArtifact sma, IWorkPage fromState, IWorkPage toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      if (sma.isTeamWorkflow() && AtsWorkDefinitions.isForceAssigneesToTeamLeads(sma.getStateDefinitionByName(toState.getPageName()))) {
         Collection<User> teamLeads = ((TeamWorkFlowArtifact) sma).getTeamDefinition().getLeads();
         if (!teamLeads.isEmpty()) {
            sma.getStateMgr().setAssignees(teamLeads);
            sma.persist(transaction);
         }
      }
   }

}
