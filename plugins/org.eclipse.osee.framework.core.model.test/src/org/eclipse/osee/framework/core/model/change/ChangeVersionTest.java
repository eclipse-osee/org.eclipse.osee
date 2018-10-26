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
package org.eclipse.osee.framework.core.model.change;

import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.mocks.ChangeTestUtility;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ChangeVersion}
 *
 * @author Roberto E. Escobar
 */
public class ChangeVersionTest {

   @Test
   public void testConstruction() {
      ChangeVersion actual = new ChangeVersion();
      ChangeVersion expected = new ChangeVersion();
      ChangeTestUtility.checkChange(expected, actual);

      actual = new ChangeVersion(GammaId.valueOf(45L), ModificationType.NEW, ApplicabilityToken.BASE);
      expected.setValue(null);
      expected.setGammaId(GammaId.valueOf(45L));
      expected.setModType(ModificationType.NEW);
      expected.setApplicabilityToken(ApplicabilityToken.BASE);
      ChangeTestUtility.checkChange(expected, actual);

      actual = new ChangeVersion("hello", GammaId.valueOf(47L), ModificationType.MERGED, ApplicabilityToken.BASE);
      expected.setValue("hello");
      expected.setGammaId(GammaId.valueOf(47L));
      expected.setModType(ModificationType.MERGED);
      expected.setApplicabilityToken(ApplicabilityToken.BASE);
      ChangeTestUtility.checkChange(expected, actual);
   }

   @Test
   public void testCopy() {
      ChangeVersion expected =
         new ChangeVersion("hello", GammaId.valueOf(47L), ModificationType.MERGED, ApplicabilityToken.BASE);
      ChangeVersion actual = new ChangeVersion();
      actual.copy(expected);
      ChangeTestUtility.checkChange(expected, actual);

      expected = new ChangeVersion(null, GammaId.valueOf(47L), ModificationType.MERGED, ApplicabilityToken.BASE);
      actual.copy(expected);
      ChangeTestUtility.checkChange(expected, actual);
   }

   @Test
   public void testExists() {
      ChangeVersion actual = new ChangeVersion();
      Assert.assertFalse(actual.isValid());

      actual.setGammaId(GammaId.valueOf(45L));
      Assert.assertFalse(actual.isValid());

      actual.setModType(ModificationType.MODIFIED);
      Assert.assertTrue(actual.isValid());
   }

   @Test
   public void testEquals() {
      ChangeVersion actual1 =
         new ChangeVersion("hello", GammaId.valueOf(47L), ModificationType.MERGED, ApplicabilityToken.BASE);
      ChangeVersion actual2 =
         new ChangeVersion("hello", GammaId.valueOf(47L), ModificationType.MERGED, ApplicabilityToken.BASE);
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
      ChangeVersion actual1 =
         new ChangeVersion("hello", GammaId.valueOf(47L), ModificationType.MERGED, ApplicabilityToken.BASE);
      Assert.assertEquals("[47,Merged,Base]", actual1.toString());
   }
}