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
package org.eclipse.osee.framework.skynet.core.test.commit;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ChangeVersion;

/**
 * @author Roberto E. Escobar
 */
public class CommitUtil {

   private CommitUtil() {
   }

   public static void checkChange(String message, ChangeVersion expected, ChangeVersion actual) {
      Assert.assertEquals(message, expected.getGammaId(), actual.getGammaId());
      Assert.assertEquals(message, expected.getModType(), actual.getModType());
   }

   public static ChangeItem createItem(int itemId, ChangeVersion base, ChangeVersion first, ChangeVersion current, ChangeVersion destination, ChangeVersion net) {
      MockChangeItem change =
            new MockChangeItem(current.getGammaId(), current.getModType(), current.getTransactionNumber());
      change.setItemId(itemId);
      if (base != null) {
         change.getBase().setModType(base.getModType());
         change.getBase().setGammaId(base.getGammaId());
      }
      if (first != null) {
         change.getFirst().setGammaId(first.getGammaId());
         change.getFirst().setModType(first.getModType());
      }
      if (destination != null) {
         change.getDestination().setGammaId(destination.getGammaId());
         change.getDestination().setModType(destination.getModType());
      }
      if (net != null) {
         change.getNet().setGammaId(net.getGammaId());
         change.getNet().setModType(net.getModType());
      }
      Assert.assertNotNull(change);
      Assert.assertNotNull(change.getBase());
      Assert.assertNotNull(change.getFirst());
      Assert.assertNotNull(change.getCurrent());
      Assert.assertNotNull(change.getDestination());
      Assert.assertNotNull(change.getNet());
      return change;
   }

   private static final class MockChangeItem extends ChangeItem {

      protected MockChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, long currentSourceTansactionNumber) {
         super(currentSourceGammaId, currentSourceModType, currentSourceTansactionNumber);
      }

      @Override
      public void setItemId(int itemId) {
         super.setItemId(itemId);
      }
   }
}
