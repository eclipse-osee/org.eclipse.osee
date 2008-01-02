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

import java.sql.SQLException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassXViewer.Extra_Columns;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeColumn;

public class MassLabelProvider implements ITableLabelProvider {

   private final MassXViewer treeViewer;

   public MassLabelProvider(MassXViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      Artifact artifact = ((MassArtifactItem) element).getArtifact();
      if (artifact == null || artifact.isDeleted()) return "";
      // Handle case where columns haven't been loaded yet
      if (columnIndex > (getTreeViewer().getTree().getColumns().length - 1)) {
         return "";
      }
      TreeColumn treeCol = getTreeViewer().getTree().getColumns()[columnIndex];
      String colName = treeCol.getText();
      if (colName.equals(Extra_Columns.HRID.name()))
         return artifact.getHumanReadableId();
      else if (colName.equals(Extra_Columns.GUID.name()))
         return artifact.getGuid();
      else if (colName.equals(Extra_Columns.Artifact_Type.name())) return artifact.getArtifactTypeName();
      if (!artifact.isAttributeTypeValid(colName)) return "";
      try {
         if (artifact.getAttributeManager(colName).getDescriptor().getBaseAttributeClass().equals(DateAttribute.class)) {
            if (artifact.getAttributeManager(colName).getAttributes().size() > 0) return ((DateAttribute) artifact.getAttributeManager(
                  colName).getAttributes().iterator().next()).getStringValue(DateAttribute.MMDDYYHHMM);
            return "";
         }

         return artifact.getAttributesToString(colName);
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         return ex.getLocalizedMessage();
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
      Artifact artifact = ((MassArtifactItem) element).getArtifact();
      if (artifact == null || artifact.isDeleted()) return null;
      if (columnIndex == 0) return artifact.getImage();
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
