/*********************************************************************
 * Copyright (c) 2010 Boeing
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
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.core.column.CompletedCancelledDateColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;

/**
 * @author Donald G. Dunne
 */
public class CompletedCancelledDateColumnUI extends XViewerAtsCoreCodeXColumn {

   public static CompletedCancelledDateColumnUI instance = new CompletedCancelledDateColumnUI();

   public static CompletedCancelledDateColumnUI getInstance() {
      return instance;
   }

   public CompletedCancelledDateColumnUI() {
      super(AtsColumnTokensDefault.CompletedCancelledDateColumn, AtsApiService.get());
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return CompletedCancelledDateColumn.getCompletedCancelledDate(element);
   }
}