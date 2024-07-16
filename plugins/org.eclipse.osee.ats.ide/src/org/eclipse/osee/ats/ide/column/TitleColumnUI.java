/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;

/**
 * @author Donald G. Dunne
 */
public class TitleColumnUI extends XViewerAtsCoreCodeXColumn {

   public static TitleColumnUI instance = new TitleColumnUI();

   public static TitleColumnUI getInstance() {
      return instance;
   }

   private TitleColumnUI() {
      super(AtsColumnTokensDefault.TitleColumn, AtsApiService.get());
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TitleColumnUI copy() {
      TitleColumnUI newXCol = new TitleColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof TaskEstDefinition) {
         return ((TaskEstDefinition) element).getName();
      } else if (element instanceof IAtsObject) {
         return ((IAtsObject) element).getName();
      }
      return "";
   }

}
