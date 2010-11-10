/*
 * Created on Oct 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.field;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.DeadlineManager;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class EstimatedCompletionDateColumn extends XViewerAtsAttributeValueColumn {

   public static EstimatedCompletionDateColumn instance = new EstimatedCompletionDateColumn();

   public static EstimatedCompletionDateColumn getInstance() {
      return instance;
   }

   public EstimatedCompletionDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedCompletionDate",
         AtsAttributeTypes.EstimatedCompletionDate, 80, SWT.LEFT, false, SortDataType.Date, true);
   }

   public EstimatedCompletionDateColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      super(attributeType, width, align, show, sortDataType, multiColumnEditable);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedCompletionDateColumn copy() {
      return new EstimatedCompletionDateColumn(getAttributeType(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable());
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (!(element instanceof IWorldViewArtifact)) {
            return null;
         }
         if (isWorldViewEcdAlerting(element).isTrue()) {
            return ImageManager.getImage(FrameworkImage.WARNING);
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static Result isWorldViewEcdAlerting(Object object) throws OseeCoreException {
      if (object instanceof ActionArtifact) {
         for (TeamWorkFlowArtifact team : ((ActionArtifact) object).getTeamWorkFlowArtifacts()) {
            Result result = isWorldViewEcdAlerting(team);
            if (result.isTrue()) {
               return result;
            }
         }
      } else if (object instanceof AbstractWorkflowArtifact) {
         return DeadlineManager.isEcdDateAlerting((AbstractWorkflowArtifact) object);
      }
      return Result.FalseResult;
   }

   public static Date getDate(Object object) throws OseeCoreException {
      if (object instanceof ActionArtifact) {
         return getDate(((ActionArtifact) object).getTeamWorkFlowArtifacts().iterator().next());
      } else if (object instanceof TeamWorkFlowArtifact) {
         Date date =
            ((TeamWorkFlowArtifact) object).getSoleAttributeValue(AtsAttributeTypes.EstimatedCompletionDate, null);
         if (date == null) {
            date = EstimatedReleaseDateColumn.getDateFromWorkflow(object);
         }
         if (date == null) {
            date = EstimatedReleaseDateColumn.getDateFromTargetedVersion(object);
         }
         return date;
      } else if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            return getDate(teamArt);
         }
      }
      return null;
   }

   public static String getDateStr(AbstractWorkflowArtifact artifact) throws OseeCoreException {
      return DateUtil.getMMDDYY(getDate(artifact));
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return DateUtil.getMMDDYY(getDate(element));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "";
   }
}
