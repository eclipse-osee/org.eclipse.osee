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

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class CoverageMergeXViewerFactoryImport extends CoverageMergeXViewerFactory {

   protected static String NAMESPACE = "osee.ats.CoverageMergeImport";

   public static XViewerColumn Import =
         new XViewerColumn(NAMESPACE + ".name", "Import", 65, SWT.LEFT, true, SortDataType.Check, false, "");

   public CoverageMergeXViewerFactoryImport() {
      super();
      clearColumnRegistration();
      registerColumns(Name, Method_Number, Execution_Number, Import, Parent_Coverage_Unit, Namespace, Coverage_Percent,
            Coverage_Method, Line_Number, Coverage_Test_Units, Assignees_Col, Location, Guid);
      for (XViewerColumn xCol : getColumns()) {
         overrideShowDefault(xCol.getId(), xCol.equals(CoverageXViewerFactory.Name) ||
         //
         xCol.equals(CoverageXViewerFactory.Method_Number) ||
         //
         xCol.equals(CoverageXViewerFactory.Execution_Number) ||
         //
         xCol.equals(CoverageXViewerFactory.Parent_Coverage_Unit) ||
         //
         xCol.equals(CoverageXViewerFactory.Coverage_Percent) ||
         //
         xCol.equals(CoverageXViewerFactory.Namespace) ||
         //
         xCol.equals(Import) ||
         //
         xCol.equals(CoverageXViewerFactory.Coverage_Method));
      }
   }

}
