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
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class XBranchLabelProvider extends XViewerLabelProvider {

   Font font = null;
   private final BranchXViewer branchXViewer;

   public XBranchLabelProvider(BranchXViewer branchXViewer) {
      super(branchXViewer);
      this.branchXViewer = branchXViewer;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn cCol, int columnIndex) throws OseeCoreException {
            try {
         if (!(element instanceof Branch)) return "";
         Branch data = (Branch) element;

         if (cCol.equals(BranchXViewerFactory.transaction)) {
            return String.valueOf(data.getBranchName());
         } else if (cCol.equals(BranchXViewerFactory.gamma)) {
            return String.valueOf(data.getBranchName());
         } else if (cCol.equals(BranchXViewerFactory.itemType)) {
            return String.valueOf(data.getBranchName());
         } else if (cCol.equals(BranchXViewerFactory.was)) {
            return String.valueOf(data.getBranchName());
         } else if (cCol.equals(BranchXViewerFactory.is)) {
            return String.valueOf(data.getBranchName());
         } else if (cCol.equals(BranchXViewerFactory.timeStamp)) {
            return String.valueOf(data.getBranchName());
         } else if (cCol.equals(BranchXViewerFactory.author)) {
            return String.valueOf(data.getBranchName());
         }else if (cCol.equals(BranchXViewerFactory.comment)) {
            return String.valueOf(data.getBranchName());
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

   public BranchXViewer getTreeViewer() {
      return branchXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException {
//      try {
//         if (!(element instanceof Branch)) return null;
//         Branch change = (Branch) element;
//         if (xCol.equals(BranchXViewerFactory.transaction)) {
//            try {
//               return SkynetGuiPlugin.getInstance().getImage("DBiconBlue.GIF");
//            } catch (IllegalArgumentException ex) {
//               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
//            } catch (Exception ex) {
//               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
//            }
//         } else if (xCol.equals(BranchXViewerFactory.itemType)) {
//            return change.getChangeImage();
//         }
//
//      } catch (Exception ex) {
//         // do nothing
//      }
      return null;
   }
}
