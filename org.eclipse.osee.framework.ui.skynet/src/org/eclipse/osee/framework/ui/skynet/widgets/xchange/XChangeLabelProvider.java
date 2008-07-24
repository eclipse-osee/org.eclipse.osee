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
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XChangeLabelProvider extends XViewerLabelProvider {

   Font font = null;
   private final ChangeXViewer changeXViewer;

   public XChangeLabelProvider(ChangeXViewer changeXViewer) {
      super(changeXViewer);
      this.changeXViewer = changeXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn cCol, int columnIndex) throws OseeCoreException, SQLException {
      try {
         if (!(element instanceof Change)) return "";
         Change change = (Change) element;
         if (cCol.equals(ChangeXViewerFactory.Name)) {
            return change.getName();
         } else if (cCol.equals(ChangeXViewerFactory.Change_Type)) {
            return change.getModificationType().getDisplayName();
         } else if (cCol.equals(ChangeXViewerFactory.Item_Kind)) {
            return change.getItemKind();
         } else if (cCol.equals(ChangeXViewerFactory.Item_Type)) {
            return change.getItemTypeName();
         } else if (cCol.equals(ChangeXViewerFactory.Is_Value)) {
            return change.getIsValue();
         } else if (cCol.equals(ChangeXViewerFactory.Was_Value)) {
            return change.getWasValue();
         } else if (cCol.equals(ChangeXViewerFactory.Artifact_Type)) {
            return change.getArtifact().getArtifactTypeName();
         } else if (cCol.equals(ChangeXViewerFactory.Hrid)) {
            return change.getArtifact().getHumanReadableId();
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "unhandled column";
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

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException, SQLException {
      try {
         if (!(element instanceof Change)) return null;
         Change change = (Change) element;
         if (xCol.equals(ChangeXViewerFactory.Name)) {
            try {
               return change.getItemKindImage();
            } catch (IllegalArgumentException ex) {
               OSEELog.logException(XMergeContentProvider.class, ex, true);
            } catch (Exception ex) {
               OSEELog.logException(XMergeContentProvider.class, ex, true);
            }
         } else if (xCol.equals(ChangeXViewerFactory.Item_Type)) {
            return change.getItemTypeImage();
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }
}
