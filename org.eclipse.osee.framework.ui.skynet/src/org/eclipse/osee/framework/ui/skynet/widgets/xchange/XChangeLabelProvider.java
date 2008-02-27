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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.sql.SQLException;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.AttributeChanged;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XChangeLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final ChangeXViewer changeXViewer;

   public XChangeLabelProvider(ChangeXViewer changeXViewer) {
      super();
      this.changeXViewer = changeXViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      try{
		if (element instanceof Change) {
			if (columnIndex == 0) {
				return ((Change) element).getArtifactName();
			}
		}

		if (element instanceof AttributeChanged) {
			AttributeChanged attributeChange = (AttributeChanged) element;
			if (columnIndex == 0) {
				return attributeChange.getArtifactName();
			} else if (columnIndex == 1) {
				return attributeChange.getDynamicAttributeDescriptor().getName();
			} 
			else if (columnIndex == 2)
				return attributeChange.getSourceDisplayData();

		}
      }catch(SQLException exception){
    	  
      }
      return "";
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param branch
    * @param xCol
    * @param aCol
    * @return column string
    * @throws SQLException
    */
   public String getColumnText(Object element, int columnIndex, Branch branch, XViewerColumn xCol, ChangeColumn aCol) throws SQLException {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      if (aCol == ChangeColumn.Artifact_Name) {
         return "Artifact";
      } else if (aCol == ChangeColumn.Attribute_Name) {
         return "Attribute";
      } else if (aCol == ChangeColumn.Value)
         return "Value";
      return "Unhandled Column";
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public ChangeXViewer getTreeViewer() {
      return changeXViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      XViewerColumn xCol = changeXViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      ChangeColumn dCol = ChangeColumn.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display
      
      if (element instanceof Change) {
          Change change = (Change) element;

          if (dCol == ChangeColumn.Artifact_Name) {
             try {
                return change.getArtifactImage();
             } catch (IllegalArgumentException ex) {
                OSEELog.logException(XMergeContentProvider.class, ex, true);
             } catch (SQLException ex) {
                OSEELog.logException(XMergeContentProvider.class, ex, true);
             }
          } else if (dCol == ChangeColumn.Attribute_Name) {
             return change.getImage();
          } 
      }
      
      return null;
   }
}
