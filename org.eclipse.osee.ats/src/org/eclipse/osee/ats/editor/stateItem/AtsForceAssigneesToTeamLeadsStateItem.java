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
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.AtsStateItem;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class AtsForceAssigneesToTeamLeadsStateItem extends AtsStateItem {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getId()
    */
   public String getId() {
      return AtsStateItem.ALL_STATE_IDS;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.AtsStateItem#transitioned(org.eclipse.osee.ats.editor.SMAManager,
    *      java.lang.String, java.lang.String, java.util.Collection)
    */
   @Override
   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      super.transitioned(smaMgr, fromState, toState, toAssignees, transaction);
      if ((smaMgr.getSma() instanceof TeamWorkFlowArtifact) && (AtsWorkDefinitions.isForceAssigneesToTeamLeads(smaMgr.getWorkPageDefinitionByName(toState)))) {
         // Set Assignees to all user roles users
         try {
            Collection<User> teamLeads = ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getLeads();
            smaMgr.getStateMgr().setAssignees(teamLeads);
            smaMgr.getSma().persistAttributes(transaction);
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IAtsStateItem#getDescription()
    */
   public String getDescription() throws OseeCoreException {
      return "AtsForceAssigneesToTeamLeadsStateItem";
   }

}
