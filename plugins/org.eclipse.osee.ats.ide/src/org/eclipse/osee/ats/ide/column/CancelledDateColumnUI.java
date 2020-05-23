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
import org.eclipse.osee.ats.core.column.CancelledDateColumn;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;

/**
 * @author Donald G. Dunne
 */
public class CancelledDateColumnUI extends XViewerAtsColumnIdColumn {

   public static CancelledDateColumnUI instance = new CancelledDateColumnUI();

   public static CancelledDateColumnUI getInstance() {
      return instance;
   }

   public CancelledDateColumnUI() {
      super(AtsColumnToken.CancelledDateColumn);
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return CancelledDateColumn.getCancelledDate(element);
   }
}