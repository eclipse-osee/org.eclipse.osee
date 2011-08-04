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

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.core.action.ActionManager;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class OperationalImpactDesciptionColumn extends XViewerValueColumn {

   public static OperationalImpactDesciptionColumn instance = new OperationalImpactDesciptionColumn();

   public static OperationalImpactDesciptionColumn getInstance() {
      return instance;
   }

   private OperationalImpactDesciptionColumn() {
      super("ats.Operational Impact Description", "Operational Impact Description", 150, SWT.LEFT, false,
         SortDataType.String, true, "What is the operational impact to the product.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OperationalImpactDesciptionColumn copy() {
      OperationalImpactDesciptionColumn newXCol = new OperationalImpactDesciptionColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
            return ((TeamWorkFlowArtifact) element).getSoleAttributeValue(
               AtsAttributeTypes.OperationalImpactDescription, "");
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action) && ActionManager.getTeams(element).size() == 1) {
            return getColumnText(ActionManager.getFirstTeam(element), column, columnIndex);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}
