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
import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.core.column.CompletedByColumn;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;

/**
 * @author Donald G. Dunne
 */
public class CompletedByColumnUI extends XViewerAtsColumnIdColumn {

   public static CancelledByColumnUI instance = new CancelledByColumnUI();

   public static CancelledByColumnUI getInstance() {
      return instance;
   }

   public CompletedByColumnUI() {
      super(AtsColumnToken.CompletedByColumn);
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return CompletedByColumn.getCompletedBy(element, AtsClientService.get());
   }
}