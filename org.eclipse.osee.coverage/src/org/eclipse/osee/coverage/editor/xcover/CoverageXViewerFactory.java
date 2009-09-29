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
package org.eclipse.osee.coverage.editor.xcover;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class CoverageXViewerFactory extends SkynetXViewerFactory {

   private static String NAMESPACE = "osee.ats.Coverage";

   public static XViewerColumn Parent_Coverage_Unit =
         new XViewerColumn(NAMESPACE + ".parentCoverageUnit", "Parent Coverage Unit", 80, SWT.LEFT, true,
               SortDataType.String, false, "");
   public static XViewerColumn Coverage_Unit =
         new XViewerColumn(NAMESPACE + ".coverageUnit", "Coverage Unit", 80, SWT.LEFT, true, SortDataType.String,
               false, "");
   public static XViewerColumn Method_Number =
         new XViewerColumn(NAMESPACE + ".methodNumber", "Method Number", 80, SWT.LEFT, true, SortDataType.String,
               false, "");
   public static XViewerColumn Execution_Number =
         new XViewerColumn(NAMESPACE + ".executionNumber", "Execution Line Number", 80, SWT.LEFT, true,
               SortDataType.String, false, "");
   public static XViewerColumn Line_Number =
         new XViewerColumn(NAMESPACE + ".lineNumber", "File Line Number", 80, SWT.LEFT, true, SortDataType.String,
               false, "");
   public static XViewerColumn Coverage_Method =
         new XViewerColumn(NAMESPACE + ".coverateMethod", "Coverage Method", 80, SWT.LEFT, true, SortDataType.String,
               false, "");
   public static XViewerColumn Test_Units =
         new XViewerColumn(NAMESPACE + ".testUnits", "Test Units", 80, SWT.LEFT, true, SortDataType.String, false, "");
   public static XViewerColumn User_Col =
         new XViewerColumn(NAMESPACE + ".user", "User", 100, SWT.LEFT, true, SortDataType.String, false, null);

   public CoverageXViewerFactory() {
      super(NAMESPACE);
      registerColumns(Parent_Coverage_Unit, Coverage_Unit, Method_Number, Execution_Number, Line_Number,
            Coverage_Method, Test_Units, User_Col);
   }
}
