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
public class ChangeItemTestUtil {

   private ChangeItemTestUtil() {
   }

   public static ChangeVersion createChange(Long long1, ModificationType mod1) {
      return new ChangeVersion(long1, mod1, 0);
   }

   public static void checkChange(String message, ChangeVersion expected, ChangeVersion actual) {
      Assert.assertEquals(message, expected.getGammaId(), actual.getGammaId());
      Assert.assertEquals(message, expected.getModType(), actual.getModType());

      Assert.assertEquals(message, expected.getValue(), actual.getValue());
      Assert.assertEquals(message, expected.getTransactionNumber(), actual.getTransactionNumber());
   }

   public static void checkChange(ChangeVersion expected, ChangeVersion actual) {
      checkChange(null, expected, actual);
   }

   public static ChangeItem createItem(int itemId, ChangeVersion base, ChangeVersion first, ChangeVersion current, ChangeVersion destination, ChangeVersion net) {
      MockChangeItem change =
            new MockChangeItem(current.getGammaId(), current.getModType(), current.getTransactionNumber());
      change.setItemId(itemId);
      if (base != null) {
         change.getBase().copy(base);
      }
      if (first != null) {
         change.getFirst().copy(first);
      }
      if (destination != null) {
         change.getDestination().copy(destination);
      }
      if (net != null) {
         change.getNet().copy(net);
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

      protected MockChangeItem(long currentSourceGammaId, ModificationType currentSourceModType, int currentSourceTransactionNumber) {
         super(currentSourceGammaId, currentSourceModType, currentSourceTransactionNumber);
      }

      @Override
      public void setItemId(int itemId) {
         super.setItemId(itemId);
      }
   }
}
