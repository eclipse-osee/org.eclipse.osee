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
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.VersionedChange;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem.GammaKind;

/**
 * @author Roberto E. Escobar
 */
public class CommitUtil {

   private CommitUtil() {
   }

   public static void checkChange(String message, VersionedChange expected, VersionedChange actual) {
      Assert.assertEquals(message, expected.getGammaId(), actual.getGammaId());
      Assert.assertEquals(message, expected.getModType(), actual.getModType());
   }

   public static ChangeItem createItem(int itemId, VersionedChange base, VersionedChange first, VersionedChange current, VersionedChange destination, VersionedChange net) {
      GammaKind[] kinds = GammaKind.values();
      return createItem(kinds[itemId % kinds.length], itemId, base, first, current, destination, net);
   }

   public static ChangeItem createItem(GammaKind gammaKind, int itemId, VersionedChange base, VersionedChange first, VersionedChange current, VersionedChange destination, VersionedChange net) {
      ChangeItem change = new ChangeItem(current.getGammaId(), current.getModType(), current.getTransactionNumber());
      change.setItemId(itemId);

      change.setKind(gammaKind);

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
      return change;
   }
}
