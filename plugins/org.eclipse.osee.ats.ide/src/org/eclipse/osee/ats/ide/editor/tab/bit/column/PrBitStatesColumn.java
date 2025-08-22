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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

public class PrBitStatesColumn extends AbstractPrBitColumnUI {

   public static PrBitStatesColumn instance = new PrBitStatesColumn();

   public static PrBitStatesColumn getInstance() {
      return instance;
   }

   public PrBitStatesColumn() {
      super(AtsColumnTokensDefault.PrBitStatesColumn, AtsAttributeTypes.BitState);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PrBitStatesColumn copy() {
      PrBitStatesColumn newXCol = new PrBitStatesColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
