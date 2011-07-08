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
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.action.ActionManager;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.swt.SWT;

public class ImplementorColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static ImplementorColumn instance = new ImplementorColumn();

   public static ImplementorColumn getInstance() {
      return instance;
   }

   private ImplementorColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".implementer", "Implementer", 80, SWT.LEFT, false,
         SortDataType.String, false, "User assigned to the Implementation of the changes.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ImplementorColumn copy() {
      ImplementorColumn newXCol = new ImplementorColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            return Artifacts.toString("; ", ((AbstractWorkflowArtifact) element).getImplementers());
         } else if (Artifacts.isOfType(element, AtsArtifactTypes.Action)) {
            Set<IBasicUser> users = new HashSet<IBasicUser>();
            for (TeamWorkFlowArtifact team : ActionManager.getTeams(element)) {
               users.addAll(team.getImplementers());
            }
            return Artifacts.toString(";", users);

         }
      } catch (OseeCoreException ex) {
         XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}
