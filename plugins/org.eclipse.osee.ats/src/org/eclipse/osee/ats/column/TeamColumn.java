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

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class TeamColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static TeamColumn instance = new TeamColumn();

   public static TeamColumn getInstance() {
      return instance;
   }

   private TeamColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".team", "Team", 50, SWT.LEFT, true, SortDataType.String, false,
         "Team that has been assigned to work this Action.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TeamColumn copy() {
      TeamColumn newXCol = new TeamColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      return getName(element);
   }

   public static String getName(Object element) {
      try {
         if (Artifacts.isOfType(element, AtsArtifactTypes.TeamWorkflow)) {
            return ((TeamWorkFlowArtifact) element).getTeamName();
         } else if (element instanceof AbstractWorkflowArtifact) {
            return getName(((AbstractWorkflowArtifact) element).getParentTeamWorkflow());
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }
}
