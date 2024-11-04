/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.column;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.swt.graphics.Color;

/**
 * @author Donald G. Dunne
 */
public class NotesColumnUI extends XViewerAtsCoreCodeXColumn {

   public static NotesColumnUI instance = new NotesColumnUI();

   public static NotesColumnUI getInstance() {
      return instance;
   }

   private NotesColumnUI() {
      super(AtsColumnTokensDefault.NotesColumn, AtsApiService.get());
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerAtsCoreCodeXColumn copy() {
      NotesColumnUI newXCol = new NotesColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) element;
         return NotesColorColumnUI.getWorkItemForground(workItem);
      }
      return super.getForeground(element, xCol, columnIndex);
   }

}
