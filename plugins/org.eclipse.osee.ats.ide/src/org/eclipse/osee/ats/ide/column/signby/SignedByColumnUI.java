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
package org.eclipse.osee.ats.ide.column.signby;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class SignedByColumnUI extends AbstractSignByColumnUI {

   public static SignedByColumnUI instance = new SignedByColumnUI();

   public static SignedByColumnUI getInstance() {
      return instance;
   }

   public SignedByColumnUI() {
      super(AtsAttributeTypes.SignedOffBy, AtsAttributeTypes.SignedOffByDate);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public AbstractSignByAndDateColumnUI copy() {
      SignedByColumnUI newXCol = new SignedByColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

}
