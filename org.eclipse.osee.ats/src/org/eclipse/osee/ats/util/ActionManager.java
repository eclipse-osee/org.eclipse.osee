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
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

/**
 * @author Donald G. Dunne
 */
public class ActionManager {

   public static ActionArtifact createAction(IProgressMonitor monitor, String title, String desc, ChangeType changeType, PriorityType priority, Collection<String> userComms, boolean validationRequired, Date needByDate, Collection<ActionableItemArtifact> actionableItems, SkynetTransaction transaction) throws OseeCoreException {
      // if "tt" is title, this is an action created for development. To
      // make it easier, all fields are automatically filled in for ATS developer

      if (monitor != null) {
         monitor.subTask("Creating Action");
      }
      ActionArtifact actionArt =
            (ActionArtifact) ArtifactTypeManager.addArtifact(ActionArtifact.ARTIFACT_NAME, AtsUtil.getAtsBranch());
      ActionArtifact.setArtifactIdentifyData(actionArt, title, desc, changeType, priority, userComms,
            validationRequired, needByDate);

      // Retrieve Team Definitions corresponding to selected Actionable Items
      if (monitor != null) {
         monitor.subTask("Creating WorkFlows");
      }
      Collection<TeamDefinitionArtifact> teams = TeamDefinitionArtifact.getImpactedTeamDefs(actionableItems);
      if (teams == null || teams.size() == 0) {
         StringBuffer sb = new StringBuffer("No teams returned for Action's selected Actionable Items\n");
         for (ActionableItemArtifact aia : actionableItems) {
            sb.append("Selected AI \"" + aia + "\" " + aia.getHumanReadableId() + "\n");
         }
         throw new OseeStateException(sb.toString());
      }

      // Create team workflow artifacts
      for (TeamDefinitionArtifact teamDef : teams) {
         actionArt.createTeamWorkflow(teamDef, actionableItems, teamDef.getLeads(actionableItems), transaction);
      }
      actionArt.persist(transaction);
      return actionArt;

   }

}
