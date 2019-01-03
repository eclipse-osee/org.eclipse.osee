/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import java.util.Date;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.DeadlineManager;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class DeadlineColumn extends XViewerAtsAttributeValueColumn {

   public static DeadlineColumn instance = new DeadlineColumn();

   public static DeadlineColumn getInstance() {
      return instance;
   }

   private DeadlineColumn() {
      super(AtsAttributeTypes.NeedBy, WorldXViewerFactory.COLUMN_NAMESPACE + ".deadline",
         AtsAttributeTypes.NeedBy.getUnqualifiedName(), 75, XViewerAlign.Left, true, SortDataType.Date, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public DeadlineColumn copy() {
      DeadlineColumn newXCol = new DeadlineColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         if (!AtsObjects.isAtsWorkItemOrAction(element)) {
            return null;
         }
         if (isDeadlineAlerting(element).isTrue()) {
            return ImageManager.getImage(FrameworkImage.WARNING);
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public static Result isDeadlineAlerting(Object object) {
      if (object instanceof AbstractWorkflowArtifact) {
         return DeadlineManager.isDeadlineDateAlerting((AbstractWorkflowArtifact) object);
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(object)) {
            Result result = isDeadlineAlerting(team);
            if (result.isTrue()) {
               return result;
            }
         }
      }
      return Result.FalseResult;
   }

   public static Date getDate(Object object) {
      if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         return getDate(AtsClientService.get().getWorkItemService().getFirstTeam(object));
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.TeamWorkflow)) {
         return ((TeamWorkFlowArtifact) object).getSoleAttributeValue(AtsAttributeTypes.NeedBy, null);
      } else if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            return getDate(teamArt);
         }
      }
      return null;
   }

   public static String getDateStr(AbstractWorkflowArtifact artifact) {
      return DateUtil.getMMDDYY(getDate(artifact));
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String value = super.getColumnText(element, column, columnIndex);
      if (Strings.isValid(value)) {
         return value;
      }
      try {
         return DateUtil.getMMDDYYHHMM(getDate(element));
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
   }

}
