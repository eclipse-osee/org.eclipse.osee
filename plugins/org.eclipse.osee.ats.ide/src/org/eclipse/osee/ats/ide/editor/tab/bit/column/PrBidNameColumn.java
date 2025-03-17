/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.bit.column;

import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

public class PrBidNameColumn extends AbstractBidColumnUI {

   public static PrBidNameColumn instance = new PrBidNameColumn();

   public static PrBidNameColumn getInstance() {
      return instance;
   }

   public PrBidNameColumn() {
      super(AtsColumnTokensDefault.PrBitNameColumn, CoreAttributeTypes.Name);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PrBidNameColumn copy() {
      PrBidNameColumn newXCol = new PrBidNameColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
