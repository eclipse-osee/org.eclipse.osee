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

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class OperationalImpactDesciptionColumn extends XViewerValueColumn {

   public static OperationalImpactDesciptionColumn instance = new OperationalImpactDesciptionColumn();

   public static OperationalImpactDesciptionColumn getInstance() {
      return instance;
   }

   private OperationalImpactDesciptionColumn() {
      super("ats.Operational Impact Description", "Operational Impact Description", 150, XViewerAlign.Left, false,
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
         if (Artifacts.isOfType(element,
            AtsArtifactTypes.Action) && AtsApiService.get().getWorkItemService().getTeams(element).size() == 1) {
            return getColumnText(AtsApiService.get().getWorkItemService().getFirstTeam(element), column, columnIndex);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }
}
