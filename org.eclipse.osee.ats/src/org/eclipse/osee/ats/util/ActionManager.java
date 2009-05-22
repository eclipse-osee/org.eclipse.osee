/*
 * Created on May 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
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

      if (monitor != null) monitor.subTask("Creating Action");
      ActionArtifact actionArt =
            (ActionArtifact) ArtifactTypeManager.addArtifact(ActionArtifact.ARTIFACT_NAME, AtsPlugin.getAtsBranch());
      ActionArtifact.setArtifactIdentifyData(actionArt, title, desc, changeType, priority, userComms,
            validationRequired, needByDate);

      // Retrieve Team Definitions corresponding to selected Actionable Items
      if (monitor != null) monitor.subTask("Creating WorkFlows");
      Collection<TeamDefinitionArtifact> teams = TeamDefinitionArtifact.getImpactedTeamDefs(actionableItems);
      if (teams == null || teams.size() == 0) {
         StringBuffer sb = new StringBuffer();
         for (ActionableItemArtifact aia : actionableItems)
            sb.append("Selected AI \"" + aia + "\" " + aia.getHumanReadableId() + "\n");
         throw new IllegalArgumentException(
               "No teams returned for Action's selected Actionable Items\n" + sb.toString());
      }

      // Create team workflow artifacts
      for (TeamDefinitionArtifact teamDef : teams) {
         actionArt.createTeamWorkflow(teamDef, actionableItems, teamDef.getLeads(actionableItems), transaction);
      }
      actionArt.persistAttributesAndRelations(transaction);
      return actionArt;

   }

}
