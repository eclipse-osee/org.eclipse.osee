/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This class provides for a Date where the value is either stored in Workflow or Version or both.
 */
public class AbstractWorkflowVersionDateColumn extends XViewerAtsAttributeValueColumn {

   public AbstractWorkflowVersionDateColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   public AbstractWorkflowVersionDateColumn(String id, IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(id, attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof ActionArtifact) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               String str = getColumnText(team, column, columnIndex);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString(";", strs);

         } else if (element instanceof AbstractWorkflowArtifact) {
            return getDateStr(getAttributeType(), (AbstractWorkflowArtifact) element);
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return super.getColumnText(element, column, columnIndex);
   }

   public static Date getDateFromWorkflow(IAttributeType attributeType, Object object) throws OseeCoreException {
      if (object instanceof TeamWorkFlowArtifact) {
         return ((TeamWorkFlowArtifact) object).getSoleAttributeValue(attributeType, null);
      } else if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            return getDateFromWorkflow(attributeType, teamArt);
         }
      }
      return null;
   }

   public static Date getDateFromTargetedVersion(IAttributeType attributeType, Object object) throws OseeCoreException {
      if (object instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) object;
         VersionArtifact verArt = TargetedVersionColumn.getTargetedVersion(teamArt);
         if (verArt != null) {
            return verArt.getSoleAttributeValue(attributeType, null);
         }
      } else if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            return getDateFromTargetedVersion(attributeType, teamArt);
         }
      }
      return null;
   }

   public static String getDateStrFromWorkflow(IAttributeType attributeType, AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return DateUtil.getMMDDYY(getDateFromWorkflow(attributeType, artifact));
   }

   public static String getDateStrFromTargetedVersion(IAttributeType attributeType, AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return DateUtil.getMMDDYY(getDateFromTargetedVersion(attributeType, artifact));
   }

   public static String getDateStr(IAttributeType attributeType, AbstractWorkflowArtifact artifact) throws OseeCoreException {
      String workflowDate = getDateStrFromWorkflow(attributeType, artifact);
      String versionDate = getDateStrFromTargetedVersion(attributeType, artifact);
      if (Strings.isValid(workflowDate) && Strings.isValid(versionDate)) {
         return String.format("%s; [%s - %s]", workflowDate, TargetedVersionColumn.getTargetedVersion(artifact),
            versionDate);
      } else if (Strings.isValid(workflowDate)) {
         return workflowDate;
      } else if (Strings.isValid(versionDate)) {
         return versionDate;
      }
      return "";
   }
}
