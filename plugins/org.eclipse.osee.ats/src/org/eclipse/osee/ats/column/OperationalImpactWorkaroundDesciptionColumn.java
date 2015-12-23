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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class OperationalImpactWorkaroundDesciptionColumn extends XViewerValueColumn {

   public static OperationalImpactWorkaroundDesciptionColumn instance =
      new OperationalImpactWorkaroundDesciptionColumn();

   public static OperationalImpactWorkaroundDesciptionColumn getInstance() {
      return instance;
   }

   private OperationalImpactWorkaroundDesciptionColumn() {
      super("ats.Operational Impact Workaround Description", "Operational Impact Workaround Description", 150, SWT.LEFT,
         false, SortDataType.String, true, "What is the workaround for the operational impact to the product.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OperationalImpactWorkaroundDesciptionColumn copy() {
      OperationalImpactWorkaroundDesciptionColumn newXCol = new OperationalImpactWorkaroundDesciptionColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
            return ((TeamWorkFlowArtifact) element).getSoleAttributeValue(
               AtsAttributeTypes.OperationalImpactWorkaroundDescription, "");
         }
         if (Artifacts.isOfType(element, AtsArtifactTypes.Action) && ActionManager.getTeams(element).size() == 1) {
            return getColumnText(ActionManager.getFirstTeam(element), column, columnIndex);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }
}
