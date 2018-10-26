/*******************************************************************************
 * Copyright (c) 200L4, 200L7 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.core.model.change;

import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.INTRODUCED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.mocks.ChangeTestUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ChangeItemUtil}
 *
 * @author Roberto E. Escobar
 */
public class ChangeItemUtilTest {

   @Test
   public void testGetSet() {
      ChangeVersion base = ChangeTestUtility.createChange(1111L, ModificationType.NEW);
      ChangeVersion first = ChangeTestUtility.createChange(2222L, ModificationType.MODIFIED);
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(4444L, ModificationType.MERGED);
      ChangeVersion net = ChangeTestUtility.createChange(5555L, ModificationType.DELETED);

      ChangeItem item = ChangeTestUtility.createItem(200L, base, first, current, destination, net);
      assertEquals(ArtifactId.valueOf(200L), item.getItemId());
      assertEquals(ArtifactTypeId.valueOf(2000L), item.getItemTypeId());
      assertEquals(ArtifactId.valueOf(20000L), item.getArtId());
      ChangeTestUtility.checkChange(base, item.getBaselineVersion());
      ChangeTestUtility.checkChange(first, item.getFirstNonCurrentChange());
      ChangeTestUtility.checkChange(current, item.getCurrentVersion());
      ChangeTestUtility.checkChange(destination, item.getDestinationVersion());
      ChangeTestUtility.checkChange(net, item.getNetChange());
   }

   @Test
   public void testHasBeenDeletedInDestination() {
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(4444L, ModificationType.DELETED);

      ChangeItem item = ChangeTestUtility.createItem(200L, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.hasBeenDeletedInDestination(item));

      item = ChangeTestUtility.createItem(200L, null, null, current, null, null);
      assertFalse(ChangeItemUtil.hasBeenDeletedInDestination(item));
   }

   @Test
   public void testAlreadyOnDestination() {
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeItem item = ChangeTestUtility.createItem(200L, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isAlreadyOnDestination(item));

      destination = ChangeTestUtility.createChange(4444L, ModificationType.INTRODUCED);
      item = ChangeTestUtility.createItem(200L, null, null, current, destination, null);
      assertFalse(ChangeItemUtil.isAlreadyOnDestination(item));

      destination = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      item = ChangeTestUtility.createItem(200L, null, null, current, destination, null);
      assertFalse(ChangeItemUtil.isAlreadyOnDestination(item));

      current = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      item = ChangeTestUtility.createItem(200L, null, null, current, destination, null);
      assertTrue(ChangeItemUtil.isAlreadyOnDestination(item));

   }

   @Test
   public void isDeletedAndDoesNotExistInDestination() {
      ChangeVersion dest, current;
      ChangeItem item;

      current = ChangeTestUtility.createChange(2222L, ModificationType.DELETED);
      dest = null;
      item = ChangeTestUtility.createItem(200L, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));

      dest = ChangeTestUtility.createChange(3333L, ModificationType.NEW);
      item = ChangeTestUtility.createItem(200L, null, null, current, dest, null);
      assertFalse(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));

      dest = ChangeTestUtility.createChange(3333L, ModificationType.SENTINEL);
      item = ChangeTestUtility.createItem(200L, null, null, current, dest, null);
      assertTrue(ChangeItemUtil.isDeletedAndDoesNotExistInDestination(item));
   }

   @Test
   public void testIgnoreCase() {
      ChangeVersion isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);

      ChangeVersion deletedCurrent = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      ChangeItem item = ChangeTestUtility.createItem(200L, null, isNew, deletedCurrent, null, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertFalse(item.getIgnoreType().isNone());

      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      item = ChangeTestUtility.createItem(200L, null, null, current, destination, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertFalse(item.getIgnoreType().isNone());

      ChangeVersion dest;

      current = ChangeTestUtility.createChange(2222L, ModificationType.DELETED);
      dest = null;
      item = ChangeTestUtility.createItem(200L, null, null, current, dest, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertFalse(item.getIgnoreType().isNone());

      // Test resurrected Cases, Deleted on Destination but resurrected (Introduced or New) on Current
      ChangeVersion baseline = ChangeTestUtility.createChange(6234L, ModificationType.DELETED);

      current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      destination = ChangeTestUtility.createChange(4444L, ModificationType.DELETED);

      item = ChangeTestUtility.createItem(200L, baseline, null, current, destination, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertTrue(item.getIgnoreType().isNone());

      current = ChangeTestUtility.createChange(3333L, ModificationType.NEW);

      item = ChangeTestUtility.createItem(200L, baseline, null, current, destination, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertTrue(item.getIgnoreType().isNone());

      // Not a resurrection case, should be IgnoreCase
      current = ChangeTestUtility.createChange(3333L, ModificationType.MODIFIED);
      baseline = ChangeTestUtility.createChange(6234L, ModificationType.MODIFIED);

      item = ChangeTestUtility.createItem(200L, baseline, null, current, destination, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertFalse(item.getIgnoreType().isNone());

      isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      destination = ChangeTestUtility.createChange(3333L, ModificationType.NEW);
      item = ChangeTestUtility.createItem(200L, null, null, isNew, destination, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertTrue(item.getIgnoreType().isNone());

      //destination Equal Or Newer Than Current
      isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      ChangeVersion isIntroduced = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      dest = ChangeTestUtility.createChange(1111L, ModificationType.NEW);
      item = ChangeTestUtility.createItem(200L, null, null, isNew, dest, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertTrue(item.getIgnoreType().isNone());

      item = ChangeTestUtility.createItem(200L, null, null, isIntroduced, dest, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertTrue(item.getIgnoreType().isNone());

      ChangeVersion isNotNew = ChangeTestUtility.createChange(2222L, ModificationType.MODIFIED);
      item = ChangeTestUtility.createItem(200L, null, null, isNotNew, dest, null);
      ChangeItemUtil.checkAndSetIgnoreCase(item);
      assertTrue(item.getIgnoreType().isNone());
   }

   @Test
   public void testCreatedAndDeleted() {
      ChangeVersion isNew = ChangeTestUtility.createChange(2222L, ModificationType.NEW);

      ChangeVersion deletedCurrent = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      ChangeItem item = ChangeTestUtility.createItem(200L, null, isNew, deletedCurrent, null, null);
      assertTrue(ChangeItemUtil.wasCreatedAndDeleted(item));

      ChangeVersion notDeletedCurrent = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      item = ChangeTestUtility.createItem(200L, null, isNew, notDeletedCurrent, null, null);
      assertFalse(ChangeItemUtil.wasCreatedAndDeleted(item));
   }

   @Test
   public void testNewOnSource() {
      ChangeVersion newType = ChangeTestUtility.createChange(2222L, ModificationType.NEW);
      ChangeVersion modified = ChangeTestUtility.createChange(2223L, ModificationType.MODIFIED);

      ChangeItem item = ChangeTestUtility.createItem(200L, null, newType, modified, null, null);
      assertTrue(ChangeItemUtil.wasNewOnSource(item));

      item = ChangeTestUtility.createItem(200L, null, null, newType, null, null);
      assertTrue(ChangeItemUtil.wasNewOnSource(item));

      item = ChangeTestUtility.createItem(200L, null, modified, modified, null, null);
      assertFalse(ChangeItemUtil.wasNewOnSource(item));
   }

   @Test
   public void testIntroducedOnSource() {
      ChangeVersion introduced = ChangeTestUtility.createChange(2222L, ModificationType.INTRODUCED);
      ChangeVersion modified = ChangeTestUtility.createChange(1234L, ModificationType.MODIFIED);
      ChangeItem item;

      item = ChangeTestUtility.createItem(200L, null, introduced, modified, null, null);
      assertTrue(ChangeItemUtil.wasIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200L, null, null, introduced, null, null);
      assertTrue(ChangeItemUtil.wasIntroducedOnSource(item));

      item = ChangeTestUtility.createItem(200L, null, null, modified, null, null);
      assertFalse(ChangeItemUtil.wasIntroducedOnSource(item));
   }

   @Test
   public void testIsNewIntroducedDeleted() {
      ChangeVersion object1 = ChangeTestUtility.createChange(200L, ModificationType.SENTINEL);
      for (ModificationType modType : Arrays.asList(ARTIFACT_DELETED, DELETED, NEW, INTRODUCED)) {
         object1.setModType(ModificationType.SENTINEL);

         Assert.assertEquals(false, ChangeItemUtil.isNew(object1));
         Assert.assertEquals(false, ChangeItemUtil.isIntroduced(object1));
         Assert.assertEquals(false, ChangeItemUtil.isDeleted(object1));

         object1.setModType(modType);
         Assert.assertEquals(modType, object1.getModType());
         Assert.assertEquals(modType == NEW, ChangeItemUtil.isNew(object1));
         Assert.assertEquals(modType == INTRODUCED, ChangeItemUtil.isIntroduced(object1));
         Assert.assertEquals(modType == DELETED || modType == ARTIFACT_DELETED, ChangeItemUtil.isDeleted(object1));
      }

      Assert.assertEquals(false, ChangeItemUtil.isNew(null));
      Assert.assertEquals(false, ChangeItemUtil.isIntroduced(null));
      Assert.assertEquals(false, ChangeItemUtil.isDeleted(null));

   }

   @Test
   public void testIsModType() {
      ChangeVersion object1 = ChangeTestUtility.createChange(200L, ModificationType.SENTINEL);
      Assert.assertTrue(ChangeItemUtil.isModType(object1, ModificationType.SENTINEL));

      for (ModificationType modType : Arrays.asList(ARTIFACT_DELETED, DELETED, NEW)) {
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
      cases.add(createTriplet(0L, ModificationType.MODIFIED, 3000L, ModificationType.NEW, false));
      cases.add(createTriplet(3000L, null, 3000L, ModificationType.NEW, true));
      cases.add(createTriplet(3000L, ModificationType.MODIFIED, 0L, ModificationType.NEW, false));
      cases.add(createTriplet(3000L, ModificationType.MODIFIED, 3000L, null, true));
      cases.add(createTriplet(0L, null, 0L, null, true));
      cases.add(createTriplet(3000L, null, 3001L, null, false));

      int index = 0;
      for (Triplet<ChangeVersion, ChangeVersion, Boolean> test : cases) {
         boolean actual = ChangeItemUtil.areGammasEqual(test.getFirst(), test.getSecond());
         Assert.assertTrue("Test " + ++index + ":", test.getThird() == actual);
      }
      assertTrue(ChangeItemUtil.areGammasEqual(null, null));
   }

   @Test
   public void testGetStartingVersion() {
      ChangeVersion ver1 = ChangeTestUtility.createChange(111L, ModificationType.NEW);
      ChangeVersion ver2 = ChangeTestUtility.createChange(222L, ModificationType.MODIFIED);
      ChangeVersion ver3 = ChangeTestUtility.createChange(333L, ModificationType.DELETED);
      ChangeVersion invalid = ChangeTestUtility.createChange(999L, ModificationType.SENTINEL);

      try {
         ChangeItemUtil.getStartingVersion(null);
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

      ChangeItem item = ChangeTestUtility.createItem(1L, ver1, ver2, ver3, null, null);
      Assert.assertEquals(ver1, ChangeItemUtil.getStartingVersion(item));

      item = ChangeTestUtility.createItem(2L, invalid, ver2, ver3, null, null);
      Assert.assertEquals(ver2, ChangeItemUtil.getStartingVersion(item));

      item = ChangeTestUtility.createItem(3L, invalid, invalid, ver3, null, null);
      Assert.assertEquals(ver3, ChangeItemUtil.getStartingVersion(item));

      try {
         item = ChangeTestUtility.createItem(3L, invalid, invalid, invalid, null, null);
         ChangeItemUtil.getStartingVersion(item);
         Assert.fail("This line should not be executed");
      } catch (OseeCoreException ex) {
         Assert.assertTrue(ex instanceof OseeStateException);
      }
   }

   @Test
   public void testCopy() {
      ChangeVersion expected =
         new ChangeVersion(GammaId.valueOf(5679L), ModificationType.MERGED, ApplicabilityToken.BASE);

      ChangeVersion actual = new ChangeVersion();
      ChangeItemUtil.copy(expected, actual);

      ChangeTestUtility.checkChange(expected, actual);

      try {
         ChangeItemUtil.copy(null, expected);
         Assert.fail("Should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

      try {
         ChangeItemUtil.copy(expected, null);
         Assert.fail("Should not be executed");
      } catch (Exception ex) {
         Assert.assertTrue(ex instanceof OseeArgumentException);
      }

   }

   @Test
   public void testIsRessurectedOnNewItem() {
      ChangeVersion base = ChangeTestUtility.createChange(0L, ModificationType.SENTINEL);
      ChangeVersion first = ChangeTestUtility.createChange(0L, ModificationType.SENTINEL);
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.NEW);
      ChangeVersion destination = ChangeTestUtility.createChange(3333L, ModificationType.DELETED);
      ChangeVersion net = ChangeTestUtility.createChange(0L, null);

      ChangeItem item = ChangeTestUtility.createItem(500L, base, first, current, destination, net);

      Assert.assertFalse(ChangeItemUtil.isResurrected(item));
   }

   @Test
   public void testAreApplicabilitiesEqual() {
      ChangeVersion one = new ChangeVersion(GammaId.valueOf(100L), ModificationType.NEW, ApplicabilityToken.BASE);
      Assert.assertFalse(ChangeItemUtil.areApplicabilitiesEqual(null, one));
      Assert.assertFalse(ChangeItemUtil.areApplicabilitiesEqual(one, null));
      Assert.assertTrue(ChangeItemUtil.areApplicabilitiesEqual(one, one));
      Assert.assertTrue(ChangeItemUtil.areApplicabilitiesEqual(null, null));
      ChangeVersion two =
         new ChangeVersion(GammaId.valueOf(100L), ModificationType.NEW, new ApplicabilityToken(2L, "dummy"));
      Assert.assertFalse(ChangeItemUtil.areApplicabilitiesEqual(one, two));
   }

   @Test
   public void testHasApplicabilityChange() {
      ChangeVersion base = ChangeTestUtility.createChange(1111L, ModificationType.NEW);
      ChangeVersion first = ChangeTestUtility.createChange(2222L, ModificationType.MODIFIED);
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(4444L, ModificationType.MERGED);
      ChangeVersion net = ChangeTestUtility.createChange(5555L, ModificationType.DELETED);

      current.setApplicabilityToken(new ApplicabilityToken(789L, "TestAppl"));
      destination.setApplicabilityToken(ApplicabilityToken.BASE);

      ChangeItem item = ChangeTestUtility.createItem(200L, base, first, current, destination, net);
      Assert.assertTrue(ChangeItemUtil.hasApplicabilityChange(item));

      item.getDestinationVersion().setApplicabilityToken(new ApplicabilityToken(789L, "TestAppl"));
      Assert.assertFalse(ChangeItemUtil.hasApplicabilityChange(item));
   }

   @Test
   public void testHasApplicabilityOnlyChange() {
      ChangeVersion base = ChangeTestUtility.createChange(1111L, ModificationType.NEW);
      ChangeVersion first = ChangeTestUtility.createChange(2222L, ModificationType.MODIFIED);
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(4444L, ModificationType.MERGED);
      ChangeVersion net = ChangeTestUtility.createChange(5555L, ModificationType.DELETED);

      current.setApplicabilityToken(new ApplicabilityToken(789L, "TestAppl"));
      destination.setApplicabilityToken(ApplicabilityToken.BASE);

      ChangeItem item = ChangeTestUtility.createItem(200L, base, first, current, destination, net);
      Assert.assertTrue(ChangeItemUtil.hasApplicabilityOnlyChange(item));

      item.setApplicabilityCopy(true);
      Assert.assertFalse(ChangeItemUtil.hasApplicabilityOnlyChange(item));

      item.setApplicabilityCopy(false);
      item.getCurrentVersion().setModType(ModificationType.DELETED);
      Assert.assertFalse(ChangeItemUtil.hasApplicabilityOnlyChange(item));

      item.getCurrentVersion().setModType(ModificationType.INTRODUCED);
      item.getDestinationVersion().setApplicabilityToken(new ApplicabilityToken(789L, "TestAppl"));
      Assert.assertFalse(ChangeItemUtil.hasApplicabilityOnlyChange(item));
   }

   @Test
   public void testSplitForApplicability() {
      ChangeVersion base = ChangeTestUtility.createChange(1111L, ModificationType.NEW);
      ChangeVersion first = ChangeTestUtility.createChange(2222L, ModificationType.MODIFIED);
      ChangeVersion current = ChangeTestUtility.createChange(3333L, ModificationType.INTRODUCED);
      ChangeVersion destination = ChangeTestUtility.createChange(4444L, ModificationType.MERGED);
      ChangeVersion net = ChangeTestUtility.createChange(5555L, ModificationType.DELETED);

      current.setApplicabilityToken(new ApplicabilityToken(789L, "TestAppl"));
      destination.setApplicabilityToken(ApplicabilityToken.BASE);
      ChangeItem item = ChangeTestUtility.createItem(200L, base, first, current, destination, net);
      ChangeItem split = ChangeItemUtil.splitForApplicability(item);
      Assert.assertTrue(split.isApplicabilityCopy());
      split.setApplicabilityCopy(false);
      Assert.assertTrue(split.totalEquals(item));
   }

   private Triplet<ChangeVersion, ChangeVersion, Boolean> createTriplet(Long long1, ModificationType mod1, Long long2, ModificationType mod2, boolean expected) {
      return new Triplet<ChangeVersion, ChangeVersion, Boolean>(//
         ChangeTestUtility.createChange(long1, mod1), //
         ChangeTestUtility.createChange(long2, mod2), //
         expected);
   }
}
