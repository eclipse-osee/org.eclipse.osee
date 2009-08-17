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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

public class MassLabelProvider extends XViewerLabelProvider {

   private final MassXViewer xViewer;

   public MassLabelProvider(MassXViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws XViewerException {
      if (col == null) {
         return null;
      }
      if (columnIndex != 0 && col instanceof XViewerValueColumn) {
         return ((XViewerValueColumn) col).getColumnImage(element, col, columnIndex);
      }
      Artifact artifact = (Artifact) element;
      if (artifact == null || artifact.isDeleted()) {
         return null;
      }
      if (columnIndex == 0) {
         return ImageManager.getImage(artifact);
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) throws XViewerException {
      try {
         if (col == null) {
            return "";
         }
         if (col instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) col).getColumnText(element, col, columnIndex);
         }
         if (element instanceof String) {
            if (columnIndex == 1) {
               return (String) element;
            } else {
               return "";
            }
         }
         Artifact artifact = (Artifact) element;
         if (artifact == null || artifact.isDeleted()) {
            return "";
         }
         // Handle case where columns haven't been loaded yet
         if (columnIndex > getTreeViewer().getTree().getColumns().length - 1) {
            return "";
         }

         String colName = col.getName();
         if (!artifact.isAttributeTypeValid(colName)) {
            return "";
         }
         if (AttributeTypeManager.getType(colName).getBaseAttributeClass().equals(DateAttribute.class)) {
            try {
               return new DateAttribute().MMDDYYHHMM.format(artifact.getSoleAttributeValue(colName));
            } catch (OseeCoreException ex) {
               return "";
            }
         }

         return artifact.getAttributesToString(colName);
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public MassXViewer getTreeViewer() {
      return xViewer;
   }

   public void dispose() {
   }
}
