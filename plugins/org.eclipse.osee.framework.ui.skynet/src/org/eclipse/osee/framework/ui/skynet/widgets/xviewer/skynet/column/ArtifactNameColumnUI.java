/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
public class ArtifactNameColumnUI extends XViewerValueColumn {

   private final boolean addDeletedLabel;
   public static ArtifactNameColumnUI instance = new ArtifactNameColumnUI();

   public static ArtifactNameColumnUI getInstance() {
      return instance;
   }

   public ArtifactNameColumnUI() {
      this(false);
   }

   public ArtifactNameColumnUI(boolean show) {
      this(false, "framework.artifact.name", "Name", 150, XViewerAlign.Left, show, SortDataType.String, false, null);
   }

   public ArtifactNameColumnUI(boolean show, boolean addDeletedLabel) {
      this(addDeletedLabel, "framework.artifact.name", "Name", 150, XViewerAlign.Left, show, SortDataType.String, false,
         null);
   }

   public ArtifactNameColumnUI(boolean addDeletedLabel, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.addDeletedLabel = addDeletedLabel;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ArtifactNameColumnUI copy() {
      ArtifactNameColumnUI newXCol = new ArtifactNameColumnUI(isShow(), addDeletedLabel);
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
