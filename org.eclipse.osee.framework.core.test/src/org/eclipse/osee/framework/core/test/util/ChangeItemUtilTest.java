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
package org.eclipse.osee.framework.core.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.test.data.ChangeTestUtility;
import org.eclipse.osee.framework.core.util.ChangeItemUtil;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ChangeItemUtil}
 * 
 * @author Roberto E. Escobar
 */
public class ChangeItemUtilTest {

   public ChangeItemUtilTest() {
   }

   @Test
   public void testGetSet() {
      ChangeVersion base = ChangeTestUtility.createChange(1111L, ModificationType.NEW);
      ChangeVersion first = ChangeTestUtility.createChange(2222L, ModificationType.MODIFIED);
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(4444L, ModificationType.MERGED);
      ChangeVersion net = ChangeTestUtility.createChange(5555L, ModificationType.DELETED);

      ChangeItem item = ChangeTestUtility.createItem(200, base, first, current, destination, net);
      assertEquals(0, item.getArtId());
      assertEquals(200, item.getItemId());
      ChangeTestUtility.checkChange(base, item.getBaselineVersion());
      ChangeTestUtility.checkChange(first, item.getFirstNonCurrentChange());
      ChangeTestUtility.checkChange(current, item.getCurrentVersion());
      ChangeTestUtility.checkChange(destination, item.getDestinationVersion());
      ChangeTestUtility.checkChange(net, item.getNetChange());

      item.setArtId(400);
      assertEquals(400, item.getArtId());
   }

   @Test
   public void testHasBeenDeletedInDestination() {
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(4444L, ModificationType.DELETED);

      ChangeItem item = ChangeTestUtility.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.hasBeenDeletedInDestination(item));

      item = ChangeTestUtility.createItem(200, null, null, current, null, null);
      assertFalse(ChangeItemUtil.hasBeenDeletedInDestination(item));
   }

   @Test
   public void testAlreadyOnDestination() {
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeItem item = ChangeTestUtility.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isAlreadyOnDestination(item));

      destination = ChangeTestUtility.createChange(4444L, ModificationType.INTRODUCED);
      item = ChangeTestUtility.createItem(200, null, null, current, destination, null);
      assertFalse(ChangeItemUtil.isAlreadyOnDestination(item));

      destination = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      item = ChangeTestUtility.createItem(200, null, null, current, destination, null);
      assertFalse(ChangeItemUtil.isAlreadyOnDestination(item));

      current = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      item = ChangeTestUtility.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isAlreadyOnDestination(item));

   }

   @Test
   public void isDeletedAndDoesNotExistInDestination() {
      ChangeVersion dest, current;
      ChangeItem item;

      current = ChangeTestUtility.createChange(2222L, ModificationType.DELETED);
      dest = null;
      item = ChangeTestUtility.createItem(200, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));

      dest = ChangeTestUtility.createChange(3333L, ModificationType.NEW);
      item = ChangeTestUtility.createItem(200, null, null, current, dest, null);
      assertFalse(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));

      dest = ChangeTestUtility.createChange(3333L, null);
      item = ChangeTestUtility.createItem(200, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));
   }

   @Test
   public void testDestinationEqualOrNewerThanCurrent() {
      ChangeVersion isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      ChangeVersion isIntroduced = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      ChangeVersion destination = ChangeTestUtility.createChange(1111L, ModificationType.NEW);
      ChangeItem item = ChangeTestUtility.createItem(200, null, null, isNew, destination, null);
      assertTrue(ChangeItemUtil.isDestinationEqualOrNewerThanCurrent(item));

      item = ChangeTestUtility.createItem(200, null, null, isIntroduced, destination, null);
      assertTrue(ChangeItemUtil.isDestinationEqualOrNewerThanCurrent(item));

      ChangeVersion isNotNew = ChangeTestUtility.createChange(2222L, ModificationType.MODIFIED);
      item = ChangeTestUtility.createItem(200, null, null, isNotNew, destination, null);
      assertFalse(ChangeItemUtil.isDestinationEqualOrNewerThanCurrent(item));
   }

   @Test
   public void testIgnoreCase() {
      ChangeVersion isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);

      ChangeVersion deletedCurrent = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      ChangeItem item = ChangeTestUtility.createItem(200, null, isNew, deletedCurrent, null, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(item));

      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      item = ChangeTestUtility.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(item));

      ChangeVersion dest;

      current = ChangeTestUtility.createChange(2222L, ModificationType.DELETED);
      dest = null;
      item = ChangeTestUtility.createItem(200, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(item));

      current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      destination = ChangeTestUtility.createChange(4444L, ModificationType.DELETED);

      item = ChangeTestUtility.createItem(200, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(item));

      isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      destination = ChangeTestUtility.createChange(3333L, ModificationType.NEW);
      item = ChangeTestUtility.createItem(200, null, null, isNew, destination, null);
      assertTrue(ChangeItemUtil.isIgnoreCase(item));
   }

   @Test
   public void testCreatedAndDeleted() {
      ChangeVersion isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);

      ChangeVersion deletedCurrent = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      ChangeItem item = ChangeTestUtility.createItem(200, null, isNew, deletedCurrent, null, null);
      assertTrue(ChangeItemUtil.wasCreatedAndDeleted(item));

      ChangeVersion notDeletedCurrent = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      item = ChangeTestUtility.createItem(200, null, isNew, notDeletedCurrent, null, null);
      assertFalse(ChangeItemUtil.wasCreatedAndDeleted(item));
   }

   @Test
   public void testNewOnSource() {
      ChangeVersion newType = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      ChangeVersion modified = ChangeTestUtility.createChange(2223L, ModificationType.MODIFIED);

      ChangeItem item = ChangeTestUtility.createItem(200, null, newType, modified, null, null);
      assertTrue(ChangeItemUtil.wasNewOnSource(item));

      item = ChangeTestUtility.createItem(200, null, null, newType, null, null);
      assertTrue(ChangeItemUtil.wasNewOnSource(item));

      item = ChangeTestUtility.createItem(200, null, modified, modified, null, null);
      assertFalse(ChangeItemUtil.wasNewOnSource(item));
   }

   @Test
   public void testNewOrIntroducedOnSource() {
      ChangeVersion introduced = ChangeTestUtility.createChange(2222L, ModificationType.INTRODUCED);
      ChangeVersion modified = ChangeTestUtility.createChange(1234L, ModificationType.MODIFIED);
      ChangeItem item;

      item = ChangeTestUtility.createItem(200, null, introduced, modified, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200, null, null, introduced, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200, null, null, modified, null, null);
      assertFalse(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      ChangeVersion newType = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      modified = ChangeTestUtility.createChange(2223L, ModificationType.MODIFIED);

      item = ChangeTestUtility.createItem(200, null, newType, modified, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200, null, null, newType, null, null);
      assertTrue(ChangeItemUtil.wasNewOrIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200, null, modified, modified, null, null);
      assertFalse(ChangeItemUtil.wasNewOrIntroducedOnSource(item));
   }

   @Test
   public void testIntroducedOnSource() {
      ChangeVersion introduced = ChangeTestUtility.createChange(2222L, ModificationType.INTRODUCED);
      ChangeVersion modified = ChangeTestUtility.createChange(1234L, ModificationType.MODIFIED);
      ChangeItem item;

      item = ChangeTestUtility.createItem(200, null, introduced, modified, null, null);
      assertTrue(ChangeItemUtil.wasIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200, null, null, introduced, null, null);
      assertTrue(ChangeItemUtil.wasIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200, null, null, modified, null, null);
      assertFalse(ChangeItemUtil.wasIntroducedOnSource(item));
   }

   @Test
   public void testIsNewIntroducedDeleted() {
      ChangeVersion object1 = ChangeTestUtility.createChange(200L, null);
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
      ChangeVersion object1 = ChangeTestUtility.createChange(200L, null);
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

   @Test
   public void testGetStartingVersion() throws OseeCoreException {
      ChangeVersion ver1 = ChangeTestUtility.createChange(111L, ModificationType.NEW);
      ChangeVersion ver2 = ChangeTestUtility.createChange(222L, ModificationType.MODIFIED);
      ChangeVersion ver3 = ChangeTestUtility.createChange(333L, ModificationType.DELETED);
      ChangeVersion invalid = ChangeTestUtility.createChange(999L, null);

      try {
         ChangeItemUtil.getStartingVersion(null);
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

      ChangeItem item = ChangeTestUtility.createItem(1, ver1, ver2, ver3, null, null);
      Assert.assertEquals(ver1, ChangeItemUtil.getStartingVersion(item));

      item = ChangeTestUtility.createItem(2, invalid, ver2, ver3, null, null);
      Assert.assertEquals(ver2, ChangeItemUtil.getStartingVersion(item));

      item = ChangeTestUtility.createItem(3, invalid, invalid, ver3, null, null);
      Assert.assertEquals(ver3, ChangeItemUtil.getStartingVersion(item));

      try {
         item = ChangeTestUtility.createItem(3, invalid, invalid, invalid, null, null);
         ChangeItemUtil.getStartingVersion(item);
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeStateException);
      }
   }

   private Triplet<ChangeVersion, ChangeVersion, Boolean> createTriplet(Long long1, ModificationType mod1, Long long2, ModificationType mod2, boolean expected) {
      return new Triplet<ChangeVersion, ChangeVersion, Boolean>(//
            ChangeTestUtility.createChange(long1, mod1), //
            ChangeTestUtility.createChange(long2, mod2), //
            expected);
   }
}
