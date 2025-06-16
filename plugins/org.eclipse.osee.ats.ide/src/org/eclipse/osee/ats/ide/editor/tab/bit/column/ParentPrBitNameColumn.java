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

public class ParentPrBitNameColumn extends AbstractParentPrBitColumnUI {

   public static ParentPrBitNameColumn instance = new ParentPrBitNameColumn();

   public static ParentPrBitNameColumn getInstance() {
      return instance;
   }

   public ParentPrBitNameColumn() {
      super(AtsColumnTokensDefault.ParentPrBitNamesColumn, CoreAttributeTypes.Name);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ParentPrBitNameColumn copy() {
      ParentPrBitNameColumn newXCol = new ParentPrBitNameColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

}
