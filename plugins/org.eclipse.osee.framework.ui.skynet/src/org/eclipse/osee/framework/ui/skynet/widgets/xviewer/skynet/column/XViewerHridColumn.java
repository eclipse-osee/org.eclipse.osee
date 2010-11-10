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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerHridColumn extends XViewerValueColumn {

   public static XViewerHridColumn instance = new XViewerHridColumn();

   public static XViewerHridColumn getInstance() {
      return instance;
   }

   public XViewerHridColumn() {
      this(false);
   }

   public XViewerHridColumn(boolean show) {
      this("framework.hrid", "HRID", 75, SWT.LEFT, show, SortDataType.String, false, "Human Readable ID");
   }

   public XViewerHridColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerHridColumn copy() {
      return new XViewerHridColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getHumanReadableId();
         } else if (element instanceof Change) {
            return ((Change) element).getChangeArtifact().getHumanReadableId();
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}
