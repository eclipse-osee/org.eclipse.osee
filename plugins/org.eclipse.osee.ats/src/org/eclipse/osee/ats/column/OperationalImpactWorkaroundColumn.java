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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.core.action.ActionManager;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.SWT;

public class OperationalImpactWorkaroundColumn extends XViewerValueColumn {

   public static OperationalImpactWorkaroundColumn instance = new OperationalImpactWorkaroundColumn();

   public static OperationalImpactWorkaroundColumn getInstance() {
      return instance;
   }

   private OperationalImpactWorkaroundColumn() {
      super("ats.Operational Impact Workaround", "Operational Impact Workaround", 80, SWT.LEFT, false,
         SortDataType.String, true, "Does operational impact to the product have a workaround?");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public OperationalImpactWorkaroundColumn copy() {
      OperationalImpactWorkaroundColumn newXCol = new OperationalImpactWorkaroundColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (AtsUtil.isAtsArtifact(element)) {
         try {
            return getOperationalImpact((Artifact) element);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return XViewerCells.getCellExceptionString(ex);
         }
      }
      return "";
   }

   private String getOperationalImpact(Artifact art) throws OseeCoreException {
      if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         return ((TeamWorkFlowArtifact) art).getSoleAttributeValue(AtsAttributeTypes.OperationalImpactWorkaround, "");
      }
      if (art.isOfType(AtsArtifactTypes.Action)) {
         Set<String> strs = new HashSet<String>();
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(art)) {
            strs.add(getOperationalImpact(team));
         }
         return Collections.toString(", ", strs);
      }
      if (art.isOfType(AtsArtifactTypes.Task)) {
         return getOperationalImpact(((TaskArtifact) art).getParentTeamWorkflow());
      }
      return "";
   }

}
