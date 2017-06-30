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

import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ArtifactNameColumn extends XViewerValueColumn {

   private final boolean addDeletedLabel;

   public ArtifactNameColumn(boolean show) {
      this(false, "framework.artifact.name", "Name", 150, XViewerAlign.Left, show, SortDataType.String, false, null);
   }

   public ArtifactNameColumn(boolean show, boolean addDeletedLabel) {
      this(addDeletedLabel, "framework.artifact.name", "Name", 150, XViewerAlign.Left, show, SortDataType.String, false,
         null);
   }

   public ArtifactNameColumn(boolean addDeletedLabel, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.addDeletedLabel = addDeletedLabel;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ArtifactNameColumn copy() {
      ArtifactNameColumn newXCol = new ArtifactNameColumn(isShow(), addDeletedLabel);
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (element instanceof Artifact) {
         Artifact artifact = (Artifact) element;
         String format = "%s";
         if (addDeletedLabel && artifact.isDeleted()) {
            format = "<Deleted> %s";
         }
         return String.format(format, artifact.getName());
      } else if (element instanceof String) {
         return "";
      }
      return super.getColumnText(element, column, columnIndex);
   }
}
