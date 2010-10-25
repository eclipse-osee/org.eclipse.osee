/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.editor.xmerge;

import java.util.Arrays;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public abstract class CoverageMergeXViewerFactory extends CoverageXViewerFactory {

   public CoverageMergeXViewerFactory() {
      super();
   }

   /**
    * This view needs it's own column objects so it can show/hide accordingly.
    */
   @Override
   public void registerColumns() {
      for (XViewerColumn xCol : Arrays.asList(Name, Method_Number, Execution_Number, Namespace, Coverage_Percent,
         Coverage_Method, Work_Product_Task, Coverage_Rationale, Coverage_Test_Units, Assignees_Col, Notes_Col,
         Parent_Coverage_Unit, Line_Number, Location, Full_Path, Guid)) {
         XViewerColumn newXCol = xCol.copy();
         if (xCol.equals(CoverageXViewerFactory.Coverage_Rationale) ||
         //
         xCol.equals(CoverageXViewerFactory.Method_Number) ||
         //
         xCol.equals(CoverageXViewerFactory.Execution_Number) ||
         //
         xCol.equals(CoverageXViewerFactory.Coverage_Method) ||
         //
         xCol.equals(CoverageXViewerFactory.Coverage_Percent) ||
         //
         xCol.equals(CoverageXViewerFactory.Assignees_Col) ||
         //
         xCol.equals(CoverageXViewerFactory.Notes_Col)) {
            newXCol.setShow(true);
         }
         if (xCol.equals(CoverageXViewerFactory.Work_Product_Task)) {
            newXCol.setShow(false);
         }
         registerColumns(newXCol);
      }
   }
}
