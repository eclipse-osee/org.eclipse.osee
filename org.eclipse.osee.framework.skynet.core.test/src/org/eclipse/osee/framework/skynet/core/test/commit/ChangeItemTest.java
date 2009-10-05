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

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItem;
import org.eclipse.osee.framework.skynet.core.commit.ChangeItemUtil;
import org.eclipse.osee.framework.skynet.core.commit.ChangeVersion;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class ChangeItemTest {

   public ChangeItemTest() {
   }

   @Test
   public void testGetSet() {
      ChangeVersion base = ChangeItemTestUtil.createChange(1111L, ModificationType.NEW);
      ChangeVersion first = ChangeItemTestUtil.createChange(2222L, ModificationType.MODIFIED);
      ChangeVersion current = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeItemTestUtil.createChange(4444L, ModificationType.MERGED);
      ChangeVersion net = ChangeItemTestUtil.createChange(5555L, ModificationType.DELETED);

      ChangeItem item = ChangeItemTestUtil.createItem(200, base, first, current, destination, net);
      Assert.assertEquals(0, item.getArtId());
      Assert.assertEquals(200, item.getItemId());
      ChangeItemTestUtil.checkChange(base, item.getBase());
      ChangeItemTestUtil.checkChange(first, item.getFirst());
      ChangeItemTestUtil.checkChange(current, item.getCurrent());
      ChangeItemTestUtil.checkChange(destination, item.getDestination());
      ChangeItemTestUtil.checkChange(net, item.getNet());

      item.setArtId(400);
      Assert.assertEquals(400, item.getArtId());
   }

   @Test
   public void testHasBeenDeletedInDestination() {
      //      ChangeItemUtil.hasBeenDeletedInDestination(changeItem)
   }

   @Test
   public void testAlreadyOnDestination() {
      //      ChangeItemUtil.isAlreadyOnDestination(changeItem)
   }

   @Test
   public void test() {

      //      ChangeItemUtil.isDeletedAndDoestNotExistInDestination(changeItem);
      //      ChangeItemUtil.isDestinationEqualOrNewerThanCurrent(changeItem);
      //      ChangeItemUtil.isIgnoreCase(hasDestinationBranch, changeItem);
      //      ChangeItemUtil.wasCreatedAndDeleted(changeItem);
      //      ChangeItemUtil.wasIntroducedOnSource(changeItem);
      //      ChangeItemUtil.wasNewOnSource(changeItem);
      //      ChangeItemUtil.wasNewOrIntroducedOnSource(changeItem);
   }

   @Test
   public void testIsNewIntroducedDeleted() {
      ChangeVersion object1 = ChangeItemTestUtil.createChange(200L, null);
      for (ModificationType modType : ModificationType.values()) {
         object1.setModType(null);
         Assert.assertNull(object1.getModType());

         Assert.assertEquals(false, ChangeItemUtil.isNew(object1));
         Assert.assertEquals(false, ChangeItemUtil.isIntroduced(object1));
         Assert.assertEquals(false, ChangeItemUtil.isDeleted(object1));

         object1.setModType(modType);
         Assert.assertEquals(modType, object1.getModType());
         Assert.assertEquals(modType == ModificationType.NEW, ChangeItemUtil.isNew(object1));
         Assert.assertEquals(modType == ModificationType.INTRODUCED, ChangeItemUtil.isIntroduced(object1));
         Assert.assertEquals(modType == ModificationType.DELETED || modType == ModificationType.ARTIFACT_DELETED,
               ChangeItemUtil.isDeleted(object1));
      }

      Assert.assertEquals(false, ChangeItemUtil.isNew(null));
      Assert.assertEquals(false, ChangeItemUtil.isIntroduced(null));
      Assert.assertEquals(false, ChangeItemUtil.isDeleted(null));

   }

   @Test
   public void testIsModType() {
      ChangeVersion object1 = ChangeItemTestUtil.createChange(200L, null);
      Assert.assertNull(object1.getModType());
      Assert.assertEquals(true, ChangeItemUtil.isModType(object1, null));

      for (ModificationType modType : ModificationType.values()) {
         object1.setModType(null);
         Assert.assertNull(object1.getModType());
         object1.setModType(modType);

         Assert.assertEquals(modType, object1.getModType());
         Assert.assertTrue(ChangeItemUtil.isModType(object1, modType));
      }
      Assert.assertEquals(false, ChangeItemUtil.isModType(null, ModificationType.NEW));
   }

   @Test
   public void testGammasEqual() {
      List<Triplet<ChangeVersion, ChangeVersion, Boolean>> cases =
            new ArrayList<Triplet<ChangeVersion, ChangeVersion, Boolean>>();

      cases.add(createTriplet(3000L, ModificationType.MODIFIED, 3000L, ModificationType.NEW, true));
      cases.add(createTriplet(null, ModificationType.MODIFIED, 3000L, ModificationType.NEW, false));
      cases.add(createTriplet(3000L, null, 3000L, ModificationType.NEW, true));
      cases.add(createTriplet(3000L, ModificationType.MODIFIED, null, ModificationType.NEW, false));
      cases.add(createTriplet(3000L, ModificationType.MODIFIED, 3000L, null, true));
      cases.add(createTriplet(null, null, null, null, true));
      cases.add(createTriplet(3000L, null, 3001L, null, false));

      int index = 0;
      for (Triplet<ChangeVersion, ChangeVersion, Boolean> test : cases) {
         boolean actual = ChangeItemUtil.areGammasEqual(test.getFirst(), test.getSecond());
         Assert.assertTrue("Test " + ++index + ":", test.getThird() == actual);
      }
   }

   private Triplet<ChangeVersion, ChangeVersion, Boolean> createTriplet(Long long1, ModificationType mod1, Long long2, ModificationType mod2, boolean expected) {
      return new Triplet<ChangeVersion, ChangeVersion, Boolean>(//
            ChangeItemTestUtil.createChange(long1, mod1), //
            ChangeItemTestUtil.createChange(long2, mod2), //
            expected);
   }
}
