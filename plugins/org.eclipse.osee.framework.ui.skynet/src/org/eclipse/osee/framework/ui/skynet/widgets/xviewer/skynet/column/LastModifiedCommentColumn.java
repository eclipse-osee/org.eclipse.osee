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

import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class LastModifiedCommentColumn extends XViewerValueColumn {

   public static LastModifiedCommentColumn instance = new LastModifiedCommentColumn();

   public static LastModifiedCommentColumn getInstance() {
      return instance;
   }

   public LastModifiedCommentColumn() {
      super("framework.lastModComment", "Last Modified Comment", 100, XViewerAlign.Left, false, SortDataType.String,
         false, "Retrieves transaction comment of last attribute update of this artifact.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LastModifiedCommentColumn copy() {
      LastModifiedCommentColumn newXCol = new LastModifiedCommentColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return TransactionManager.getTransaction(((Artifact) element).getTransaction()).getComment();
         } else if (element instanceof Change) {
            return TransactionManager.getTransaction(
               ((Change) element).getChangeArtifact().getTransaction()).getComment();
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }
}
