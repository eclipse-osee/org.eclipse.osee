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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;

public class MassLabelProvider implements ITableLabelProvider {

   private final MassXViewer treeViewer;

   public MassLabelProvider(MassXViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      try {
         TreeColumn treeCol = getTreeViewer().getTree().getColumn(columnIndex);
         if (treeCol.getData() instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) treeCol.getData()).getColumnText(element,
                  (XViewerValueColumn) treeCol.getData(), columnIndex);
         }
         if (element instanceof String) {
            if (columnIndex == 1)
               return (String) element;
            else
               return "";
         }
         Artifact artifact = (Artifact) element;
         if (artifact == null || artifact.isDeleted()) return "";
         // Handle case where columns haven't been loaded yet
         if (columnIndex > (getTreeViewer().getTree().getColumns().length - 1)) {
            return "";
         }

         String colName = treeCol.getText();
         if (!artifact.isAttributeTypeValid(colName)) {
            return "";
         }
         if (AttributeTypeManager.getType(colName).getBaseAttributeClass().equals(DateAttribute.class)) {
            try {
               return DateAttribute.MMDDYYHHMM.format(artifact.getSoleAttributeValue(colName));
            } catch (OseeCoreException ex) {
               return "";
            }
         }

         return artifact.getAttributesToString(colName);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         return XViewerCells.getCellExceptionString(ex);
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
      return treeViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      try {
         TreeColumn treeCol = getTreeViewer().getTree().getColumn(columnIndex);
         if (treeCol.getData() instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) treeCol.getData()).getColumnImage(element,
                  (XViewerValueColumn) treeCol.getData(), columnIndex);
         }
         Artifact artifact = (Artifact) element;
         if (artifact == null || artifact.isDeleted()) return null;
         if (columnIndex == 0) return artifact.getImage();
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }
}
