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
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.core.column.CreatedDateColumn;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;

/**
 * @author Donald G. Dunne
 */
public class CreatedDateColumnUI extends XViewerAtsColumnIdColumn {

   public static CreatedDateColumnUI instance = new CreatedDateColumnUI();

   public static CreatedDateColumnUI getInstance() {
      return instance;
   }

   public CreatedDateColumnUI() {
      super(AtsColumnTokens.CreatedDateColumn);
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return CreatedDateColumn.getDate(element);
   }

}
