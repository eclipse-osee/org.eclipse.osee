/*********************************************************************
 * Copyright (c) 2014 Boeing
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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

/**
 * @author Donald G. Dunne
 */
public class IdColumn extends XViewerValueColumn {

   public static IdColumn instance = new IdColumn();

   public static IdColumn getInstance() {
      return instance;
   }

   public IdColumn() {
      this(false);
   }

   public IdColumn(boolean show) {
      super("framework.uuid", "Uuid", 75, XViewerAlign.Left, show, SortDataType.String, false,
         "Universally Unique Identifier");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public IdColumn copy() {
      IdColumn newXCol = new IdColumn(isShow());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof Artifact) {
         return ((Artifact) element).getIdString();
      } else if (element instanceof Change) {
         return ((Change) element).getChangeArtifact().getIdString();
      }
      return "";
   }
}