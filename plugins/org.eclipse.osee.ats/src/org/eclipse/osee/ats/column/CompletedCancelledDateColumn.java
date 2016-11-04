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
package org.eclipse.osee.ats.column;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class CompletedCancelledDateColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static CompletedCancelledDateColumn instance = new CompletedCancelledDateColumn();

   public static CompletedCancelledDateColumn getInstance() {
      return instance;
   }

   private CompletedCancelledDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".cmpCnclDate", "Completed or Cancelled Date", 80, XViewerAlign.Left, false,
         SortDataType.Date, false, "Date action to completed or cancelled.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public CompletedCancelledDateColumn copy() {
      CompletedCancelledDateColumn newXCol = new CompletedCancelledDateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) element;
            if (awa.isCompleted()) {
               return CompletedDateColumn.getDateStr(element);
            } else if (awa.isCancelled()) {
               return CancelledDateColumn.getDateStr(element);
            }
         } else if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<String> dates = new HashSet<>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               String date = getColumnText(team, column, columnIndex);
               if (Strings.isValid(date)) {
                  dates.add(date);
               }
            }
            return Artifacts.toString(";", dates);
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   public Date getDate(Object element) throws OseeCoreException {
      AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) element;
      if (awa.isCompleted()) {
         return CompletedDateColumn.getDate(element);
      } else if (awa.isCancelled()) {
         return CancelledDateColumn.getDate(element);
      }
      return null;
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (!Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
         return getDate(element);
      }
      return null;
   }

}
