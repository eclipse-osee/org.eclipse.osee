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
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsStateItem extends AtsStateItem {

   @Override
   public String getId() {
      return AtsStateItem.ALL_STATE_IDS;
   }

   @Override
   public void transitioned(StateMachineArtifact sma, String fromState, String toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      super.transitioned(sma, fromState, toState, toAssignees, transaction);
      if ((sma.isTeamWorkflow()) && (AtsWorkDefinitions.isForceAssigneesToTeamLeads(sma.getWorkPageDefinitionByName(toState)))) {
         // Set Assignees to all user roles users
         try {
            Collection<User> teamLeads = ((TeamWorkFlowArtifact) sma).getTeamDefinition().getLeads();
            sma.getStateMgr().setAssignees(teamLeads);
            sma.persist(transaction);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   public String getDescription() throws OseeCoreException {
      return "AtsForceAssigneesToTeamLeadsStateItem";
   }

}
