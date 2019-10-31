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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTokenColumn extends XViewerValueColumn {

   public static ArtifactTokenColumn instance = new ArtifactTokenColumn();

   public static ArtifactTokenColumn getInstance() {
      return instance;
   }

   public ArtifactTokenColumn() {
      super("framework.artToken", "Artifact Token", 50, XViewerAlign.Left, false, SortDataType.String, false,
         "Artifact Token formatted as needed for ORCS Writer.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ArtifactTokenColumn copy() {
      ArtifactTokenColumn newXCol = new ArtifactTokenColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      String columnText = "";
      try {
         if (element instanceof Artifact) {
            columnText = ((Artifact) element).toStringWithId();
         } else if (element instanceof Change) {
            Artifact artifact = ((Change) element).getChangeArtifact();
            columnText = artifact.isValid() ? artifact.toStringWithId() : "";
         } else {
            columnText = "";
         }
         return columnText;
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

}
