/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.test.mocks;

import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public class MockChangeItem extends ChangeItem {

   public MockChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, int currentSourceTransactionNumber) {
      super(currentSourceGammaId, currentSourceModType, currentSourceTransactionNumber);
   }

   @Override
   public void setItemId(int itemId) {
      super.setItemId(itemId);
   }
}