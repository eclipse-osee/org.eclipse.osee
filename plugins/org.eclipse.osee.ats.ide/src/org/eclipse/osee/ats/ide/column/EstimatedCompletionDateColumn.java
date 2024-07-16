/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.column;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.DeadlineManager;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttrTokenXColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class EstimatedCompletionDateColumn extends XViewerAtsAttrTokenXColumn {

   public static EstimatedCompletionDateColumn instance = new EstimatedCompletionDateColumn();

   public static EstimatedCompletionDateColumn getInstance() {
      return instance;
   }

   private EstimatedCompletionDateColumn() {
      super(AtsAttributeTypes.EstimatedCompletionDate,
         WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedCompletionDate",
         AtsAttributeTypes.EstimatedCompletionDate.getUnqualifiedName(), 80, XViewerAlign.Left, false,
         SortDataType.Date, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedCompletionDateColumn copy() {
      EstimatedCompletionDateColumn newXCol = new EstimatedCompletionDateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (!AtsObjects.isAtsWorkItemOrAction(element)) {
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

   public static Result isWorldViewEcdAlerting(Object object) {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(object)) {
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

   public static Date getDate(Object object) {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         return getDate(AtsApiService.get().getWorkItemService().getFirstTeam(object));
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.TeamWorkflow)) {
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
         AbstractWorkflowArtifact abstractWorkflowArtifact = (AbstractWorkflowArtifact) object;
         Date date = abstractWorkflowArtifact.getSoleAttributeValue(AtsAttributeTypes.EstimatedCompletionDate, null);
         if (date == null) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) abstractWorkflowArtifact.getParentTeamWorkflow();
            if (teamArt != null) {
               return getDate(teamArt);
            }
         } else {
            return date;
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return DateUtil.getMMDDYY(getDate(element));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "";
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return getDate(element);
   }

}
