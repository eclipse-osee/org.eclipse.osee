/*******************************************************************************
 * Copyright (c) 2011 Boeing.
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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteWorkflowColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteWorkflowColumn instance = new PercentCompleteWorkflowColumn();

   public static PercentCompleteWorkflowColumn getInstance() {
      return instance;
   }

   private PercentCompleteWorkflowColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".workflowPercentComplete", "Workflow Percent Complete", 40,
         SWT.CENTER, false, SortDataType.Percent, false,
         "Percent Complete for full workflow (if work definition configured for single percent).\n\nAmount entered from user.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteWorkflowColumn copy() {
      PercentCompleteWorkflowColumn newXCol = new PercentCompleteWorkflowColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof AbstractWorkflowArtifact) {
            int awaPercent =
               ((AbstractWorkflowArtifact) element).getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
            return String.valueOf(awaPercent);
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

}
