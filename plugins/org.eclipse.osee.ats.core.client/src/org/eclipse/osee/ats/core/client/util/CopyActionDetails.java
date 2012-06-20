/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.util;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

public class CopyActionDetails {

   private final AbstractWorkflowArtifact awa;
   private static final String USE_DEVELOPER_CHANGE_TYPES = "UseDeveloperChangeTypes";

   public CopyActionDetails(AbstractWorkflowArtifact awa) {
      this.awa = awa;
   }

   public String getDetailsString() {
      String detailsStr = "";
      try {
         if (awa.getParentTeamWorkflow() != null) {
            IAtsTeamDefinition teamDef = awa.getParentTeamWorkflow().getTeamDefinition();
            String formatStr = getFormatStr(teamDef);
            if (Strings.isValid(formatStr)) {
               detailsStr = formatStr;
               detailsStr = detailsStr.replaceAll("<hrid>", awa.getHumanReadableId());
               detailsStr = detailsStr.replaceAll("<name>", awa.getName());
               detailsStr = detailsStr.replaceAll("<artType>", awa.getArtifactTypeName());
               detailsStr = detailsStr.replaceAll("<changeType>", getChangeTypeOrObjectType(awa));
            }
         }
         if (!Strings.isValid(detailsStr)) {
            detailsStr =
               "\"" + awa.getArtifactTypeName() + "\" - " + awa.getHumanReadableId() + " - \"" + awa.getName() + "\"";
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return detailsStr;
   }

   private String getChangeTypeOrObjectType(AbstractWorkflowArtifact awa) throws OseeCoreException {
      String result = "";
      if (awa instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) awa;
         result = ChangeTypeUtil.getChangeTypeStr(awa);
         if (teamArt.getTeamDefinition().getStaticIds().contains(USE_DEVELOPER_CHANGE_TYPES)) {
            if (result.equals("Improvement")) {
               result = "feature";
            } else if (result.equals("Problem")) {
               result = "bug";
            }
         }
      } else if (awa instanceof TaskArtifact) {
         result = "Task";
      } else if (awa.isOfType(AtsArtifactTypes.ReviewArtifact)) {
         result = "Review";
      } else if (awa.isOfType(AtsArtifactTypes.Goal)) {
         result = "Goal";
      }
      if (!Strings.isValid(result)) {
         result = "unknown";
      }
      return result;
   }

   private String getFormatStr(IAtsTeamDefinition teamDef) throws OseeCoreException {
      if (teamDef != null) {
         String formatStr = teamDef.getActionDetailsFormat();
         if (Strings.isValid(formatStr)) {
            return formatStr;
         }
         if (teamDef.getParentTeamDef() != null) {
            return getFormatStr(teamDef.getParentTeamDef());
         }
      }
      return null;
   }
}
