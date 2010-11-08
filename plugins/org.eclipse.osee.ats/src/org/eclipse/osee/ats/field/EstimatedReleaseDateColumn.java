/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IMultiColumnEditProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;

public class EstimatedReleaseDateColumn extends XViewerAtsAttributeValueColumn implements IMultiColumnEditProvider {

   public static final IAttributeType EstimatedReleaseDate = new AtsAttributeTypes("AAMFEcy6VB7Ble5SP1QA",
      "Estimated Release Date", "Date the changes will be made available to the users.");
   public static EstimatedReleaseDateColumn instance = new EstimatedReleaseDateColumn();

   public static EstimatedReleaseDateColumn getInstance() {
      return instance;
   }

   private EstimatedReleaseDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedReleaseDate", EstimatedReleaseDate, 80, SWT.LEFT, false,
         SortDataType.Date, true);
   }

   public EstimatedReleaseDateColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
      setDescription(description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedReleaseDateColumn copy() {
      return new EstimatedReleaseDateColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof ActionArtifact) {
            Set<String> strs = new HashSet<String>();
            for (TeamWorkFlowArtifact team : ((ActionArtifact) element).getTeamWorkFlowArtifacts()) {
               strs.add(getColumnText(team, column, columnIndex));
            }
            return Collections.toString(";", strs);

         } else if (element instanceof AbstractWorkflowArtifact) {
            return getDateStr((AbstractWorkflowArtifact) element);
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return super.getColumnText(element, column, columnIndex);
   }

   public static Date getDateFromWorkflow(Object object) throws OseeCoreException {
      if (object instanceof TeamWorkFlowArtifact) {
         return ((TeamWorkFlowArtifact) object).getSoleAttributeValue(EstimatedReleaseDate, null);
      } else if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            return getDateFromWorkflow(teamArt);
         }
      }
      return null;
   }

   public static Date getDateFromTargetedVersion(Object object) throws OseeCoreException {
      if (object instanceof TeamWorkFlowArtifact) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) object;
         VersionArtifact verArt = TargetedVersionColumn.getTargetedVersion(teamArt);
         if (verArt != null) {
            return verArt.getSoleAttributeValue(EstimatedReleaseDate, null);
         }
      } else if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            return getDateFromTargetedVersion(teamArt);
         }
      }
      return null;
   }

   public static String getDateStrFromWorkflow(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return DateUtil.getMMDDYY(getDateFromWorkflow(artifact));
   }

   public static String getDateStrFromTargetedVersion(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return DateUtil.getMMDDYY(getDateFromTargetedVersion(artifact));
   }

   public static String getDateStr(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      String workflowDate = getDateStrFromWorkflow(artifact);
      String versionDate = getDateStrFromTargetedVersion(artifact);
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
