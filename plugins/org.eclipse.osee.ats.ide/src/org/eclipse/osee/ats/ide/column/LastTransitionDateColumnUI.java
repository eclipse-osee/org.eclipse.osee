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

import java.util.Date;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class LastTransitionDateColumnUI extends XViewerAtsColumn implements IXViewerValueColumn {

   public static LastTransitionDateColumnUI instance = new LastTransitionDateColumnUI();

   public static LastTransitionDateColumnUI getInstance() {
      return instance;
   }

   private LastTransitionDateColumnUI() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".lastTransitionDate", "Last Transition State", 80,
         XViewerAlign.Center, false, SortDataType.Date, false, "Date of the transition to the current state.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LastTransitionDateColumnUI copy() {
      LastTransitionDateColumnUI newXCol = new LastTransitionDateColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof IAtsWorkItem) {
            Date transitionDate =
               AtsApiService.get().getWorkItemService().getLastTransitionDate((IAtsWorkItem) element);
            if (transitionDate != null) {
               return DateUtil.getMMDDYYHHMM(transitionDate);
            }
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }
}
