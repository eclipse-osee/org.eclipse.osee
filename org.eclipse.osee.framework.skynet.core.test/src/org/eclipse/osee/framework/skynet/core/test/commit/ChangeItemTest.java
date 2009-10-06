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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
      assertEquals(0, item.getArtId());
      assertEquals(200, item.getItemId());
      ChangeItemTestUtil.checkChange(base, item.getBase());
      ChangeItemTestUtil.checkChange(first, item.getFirst());
      ChangeItemTestUtil.checkChange(current, item.getCurrent());
      ChangeItemTestUtil.checkChange(destination, item.getDestination());
      ChangeItemTestUtil.checkChange(net, item.getNet());

      item.setArtId(400);
      assertEquals(400, item.getArtId());
   }

   @Test
   public void testHasBeenDeletedInDestination() {
      ChangeVersion current = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeItemTestUtil.createChange(4444L, ModificationType.DELETED);

      ChangeItem item = ChangeItemTestUtil.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.hasBeenDeletedInDestination(item));

      item = ChangeItemTestUtil.createItem(200, null, null, current, null, null);
      assertFalse(ChangeItemUtil.hasBeenDeletedInDestination(item));
   }

   @Test
   public void testAlreadyOnDestination() {
      ChangeVersion current = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      ChangeItem item = ChangeItemTestUtil.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isAlreadyOnDestination(item));

      destination = ChangeItemTestUtil.createChange(4444L, ModificationType.INTRODUCED);
      item = ChangeItemTestUtil.createItem(200, null, null, current, destination, null);
      assertFalse(ChangeItemUtil.isAlreadyOnDestination(item));

      destination = ChangeItemTestUtil.createChange(3333L, ModificationType.DELETED);
      item = ChangeItemTestUtil.createItem(200, null, null, current, destination, null);
      assertFalse(ChangeItemUtil.isAlreadyOnDestination(item));

      current = ChangeItemTestUtil.createChange(3333L, ModificationType.DELETED);
      item = ChangeItemTestUtil.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isAlreadyOnDestination(item));

   }

   @Test
   public void isDeletedAndDoesNotExistInDestination() {
      ChangeVersion dest, current;
      ChangeItem item;

      current = ChangeItemTestUtil.createChange(2222L, ModificationType.DELETED);
      dest = null;
      item = ChangeItemTestUtil.createItem(200, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));

      dest = ChangeItemTestUtil.createChange(3333L, ModificationType.NEW);
      item = ChangeItemTestUtil.createItem(200, null, null, current, dest, null);
      assertFalse(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));

      dest = ChangeItemTestUtil.createChange(3333L, null);
      item = ChangeItemTestUtil.createItem(200, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));
   }

   @Test
   public void testDestinationEqualOrNewerThanCurrent() {
      ChangeVersion isNew = ChangeItemTestUtil.createChange(2222L, ModificationType.NEW);
      ChangeVersion isIntroduced = ChangeItemTestUtil.createChange(2222L, ModificationType.NEW);
      ChangeVersion destination = ChangeItemTestUtil.createChange(1111L, ModificationType.NEW);
      ChangeItem item = ChangeItemTestUtil.createItem(200, null, null, isNew, destination, null);
      assertTrue(ChangeItemUtil.isDestinationEqualOrNewerThanCurrent(item));

      item = ChangeItemTestUtil.createItem(200, null, null, isIntroduced, destination, null);
      assertTrue(ChangeItemUtil.isDestinationEqualOrNewerThanCurrent(item));

      ChangeVersion isNotNew = ChangeItemTestUtil.createChange(2222L, ModificationType.MODIFIED);
      item = ChangeItemTestUtil.createItem(200, null, null, isNotNew, destination, null);
      assertFalse(ChangeItemUtil.isDestinationEqualOrNewerThanCurrent(item));
   }

   @Test
   public void testIgnoreCase() {
      ChangeVersion isNew = ChangeItemTestUtil.createChange(2222L, ModificationType.NEW);

      ChangeVersion deletedCurrent = ChangeItemTestUtil.createChange(3333L, ModificationType.DELETED);
      ChangeItem item = ChangeItemTestUtil.createItem(200, null, isNew, deletedCurrent, null, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(false, item));

      ChangeVersion current = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      item = ChangeItemTestUtil.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(false, item));

      ChangeVersion dest;

      current = ChangeItemTestUtil.createChange(2222L, ModificationType.DELETED);
      dest = null;
      item = ChangeItemTestUtil.createItem(200, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(true, item));

      current = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      destination = ChangeItemTestUtil.createChange(4444L, ModificationType.DELETED);

      item = ChangeItemTestUtil.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(false, item));

      isNew = ChangeItemTestUtil.createChange(2222L, ModificationType.NEW);
      destination = ChangeItemTestUtil.createChange(3333L, ModificationType.NEW);
      item = ChangeItemTestUtil.createItem(200, null, null, isNew, destination, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(false, item));
   }

   @Test
   public void testCreatedAndDeleted() {
      ChangeVersion isNew = ChangeItemTestUtil.createChange(2222L, ModificationType.NEW);

      ChangeVersion deletedCurrent = ChangeItemTestUtil.createChange(3333L, ModificationType.DELETED);
      ChangeItem item = ChangeItemTestUtil.createItem(200, null, isNew, deletedCurrent, null, null);
      assertTrue(ChangeItemUtil.wasCreatedAndDeleted(item));

      ChangeVersion notDeletedCurrent = ChangeItemTestUtil.createChange(3333L, ModificationType.INTRODUCED);
      item = ChangeItemTestUtil.createItem(200, null, isNew, notDeletedCurrent, null, null);
      assertFalse(ChangeItemUtil.wasCreatedAndDeleted(item));
   }

   @Test
   public void testNewOnSource() {
      ChangeVersion newType = ChangeItemTestUtil.createChange(2222L, ModificationType.NEW);
      ChangeVersion modified = ChangeItemTestUtil.createChange(2223L, ModificationType.MODIFIED);

      ChangeItem item = ChangeItemTestUtil.createItem(200, null, newType, modified, null, null);
      assertTrue(ChangeItemUtil.wasNewOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, null, newType, null, null);
      assertTrue(ChangeItemUtil.wasNewOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, modified, modified, null, null);
      assertFalse(ChangeItemUtil.wasNewOnSource(item));
   }

   @Test
   public void testNewOrIntroducedOnSource() {
      ChangeVersion introduced = ChangeItemTestUtil.createChange(2222L, ModificationType.INTRODUCED);
      ChangeVersion modified = ChangeItemTestUtil.createChange(1234L, ModificationType.MODIFIED);
      ChangeItem item;

      item = ChangeItemTestUtil.createItem(200, null, introduced, modified, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, null, introduced, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, null, modified, null, null);
      assertFalse(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      ChangeVersion newType = ChangeItemTestUtil.createChange(2222L, ModificationType.NEW);
      modified = ChangeItemTestUtil.createChange(2223L, ModificationType.MODIFIED);

      item = ChangeItemTestUtil.createItem(200, null, newType, modified, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, null, newType, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, modified, modified, null, null);
      assertFalse(ChangeItemUtil.wasNewOrIntroducedOnSource(item));
   }

   @Test
   public void testIntroducedOnSource() {
      ChangeVersion introduced = ChangeItemTestUtil.createChange(2222L, ModificationType.INTRODUCED);
      ChangeVersion modified = ChangeItemTestUtil.createChange(1234L, ModificationType.MODIFIED);
      ChangeItem item;

      item = ChangeItemTestUtil.createItem(200, null, introduced, modified, null, null);
      assertTrue(ChangeItemUtil.wasIntroducedOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, null, introduced, null, null);
      assertTrue(ChangeItemUtil.wasIntroducedOnSource(item));

      item = ChangeItemTestUtil.createItem(200, null, null, modified, null, null);
      assertFalse(ChangeItemUtil.wasIntroducedOnSource(item));
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
      assertTrue(ChangeItemUtil.areGammasEqual(null, null));
   }

   private Triplet<ChangeVersion, ChangeVersion, Boolean> createTriplet(Long long1, ModificationType mod1, Long long2, ModificationType mod2, boolean expected) {
      return new Triplet<ChangeVersion, ChangeVersion, Boolean>(//
            ChangeItemTestUtil.createChange(long1, mod1), //
            ChangeItemTestUtil.createChange(long2, mod2), //
            expected);
   }
}
