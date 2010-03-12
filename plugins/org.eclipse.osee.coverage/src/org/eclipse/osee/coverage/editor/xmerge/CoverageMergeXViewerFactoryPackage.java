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
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class CoverageMergeXViewerFactoryPackage extends CoverageMergeXViewerFactory {

   protected static String NAMESPACE = "osee.ats.CoverageMergePackage";

   public CoverageMergeXViewerFactoryPackage() {
      super();
      clearColumnRegistration();
      registerMergeColumns();
   }

   @Override
   public void registerMergeColumns() {
      super.registerMergeColumns();
      for (XViewerColumn xCol : getColumns()) {
         if (xCol.equals(CoverageXViewerFactory.Coverage_Rationale) ||
         //
         xCol.equals(CoverageXViewerFactory.Method_Number) ||
         //
         xCol.equals(CoverageXViewerFactory.Execution_Number) ||
         //
         //
         xCol.equals(CoverageXViewerFactory.Coverage_Percent) ||
         //
         xCol.equals(CoverageXViewerFactory.Assignees_Col) ||
         //
         xCol.equals(CoverageXViewerFactory.Notes_Col)) {
            overrideShowDefault(xCol.getId(), true);
         }
      }
   }

}
