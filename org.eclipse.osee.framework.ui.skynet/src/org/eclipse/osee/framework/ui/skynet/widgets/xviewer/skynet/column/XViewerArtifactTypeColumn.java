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

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerArtifactTypeColumn extends XViewerValueColumn {

   public XViewerArtifactTypeColumn(String name) {
      this("framework.artifact.type." + name, name, 150, SWT.LEFT, true, SortDataType.String, false, "Artifact Type");
   }

   public XViewerArtifactTypeColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    * 
    * @param col
    */
   @Override
   public XViewerArtifactTypeColumn copy() {
      return new XViewerArtifactTypeColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
            isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (element instanceof Artifact) {
         return ((Artifact) element).getArtifactTypeName();
      }
      return super.getColumnText(element, column, columnIndex);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (element instanceof Artifact) {
         return ImageManager.getImage((Artifact) element);
      }
      return super.getColumnImage(element, column, columnIndex);
   }

}
