/*
 * Created on Oct 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.commit;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.commit.ChangeVersion;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class ChangeVersionTest {

   @Test
   public void testConstruction() {
      ChangeVersion actual = new ChangeVersion();
      ChangeVersion expected = new ChangeVersion();
      ChangeItemTestUtil.checkChange(expected, actual);

      actual = new ChangeVersion(45L, ModificationType.NEW, 54);
      expected.setValue(null);
      expected.setGammaId(45L);
      expected.setModType(ModificationType.NEW);
      expected.setTransactionNumber(54);
      ChangeItemTestUtil.checkChange(expected, actual);

      actual = new ChangeVersion("hello", 47L, ModificationType.MERGED, 46);
      expected.setValue("hello");
      expected.setGammaId(47L);
      expected.setModType(ModificationType.MERGED);
      expected.setTransactionNumber(46);
      ChangeItemTestUtil.checkChange(expected, actual);
   }

   @Test
   public void testCopy() {
      ChangeVersion expected = new ChangeVersion("hello", 47L, ModificationType.MERGED, 46);
      ChangeVersion actual = new ChangeVersion();
      actual.copy(expected);
      ChangeItemTestUtil.checkChange(expected, actual);

      expected = new ChangeVersion(null, 47L, ModificationType.MERGED, null);
      actual.copy(expected);
      ChangeItemTestUtil.checkChange(expected, actual);
   }

   @Test
   public void testExists() {
      ChangeVersion actual = new ChangeVersion();
      Assert.assertFalse(actual.isValid());

      actual.setGammaId(45L);
      Assert.assertFalse(actual.isValid());

      actual.setModType(ModificationType.MODIFIED);
      Assert.assertTrue(actual.isValid());
   }

   @Test
   public void testEquals() {
      ChangeVersion actual1 = new ChangeVersion("hello", 47L, ModificationType.MERGED, 46);
      ChangeVersion actual2 = new ChangeVersion("hello", 47L, ModificationType.MERGED, 46);
      ChangeVersion expected = new ChangeVersion();

      Assert.assertEquals(actual2, actual1);
      Assert.assertTrue(actual2.hashCode() == actual1.hashCode());

      Assert.assertTrue(!expected.equals(actual1));
      Assert.assertTrue(!expected.equals(actual2));

      Assert.assertTrue(expected.hashCode() != actual1.hashCode());
      Assert.assertTrue(expected.hashCode() != actual2.hashCode());

      expected.copy(actual1);
      Assert.assertEquals(expected, actual1);
      Assert.assertEquals(expected, actual2);

   }

   @Test
   public void testToString() {
      ChangeVersion actual1 = new ChangeVersion("hello", 47L, ModificationType.MERGED, 46);
      Assert.assertEquals("[46,47,MERGED]", actual1.toString());

      ChangeVersion expected = new ChangeVersion();
      Assert.assertEquals("[null,null,null]", expected.toString());

   }
}
