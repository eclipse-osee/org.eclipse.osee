/*
 * Created on Mar 19, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import junit.framework.Assert;
import org.eclipse.osee.ats.workdef.api.ReviewBlockType;
import org.junit.Test;

/**
 * Test case for {@link ReviewBlockType}
 *
 * @author Donald G. Dunne
 */
public class ReviewBlockTypeTest {

   @Test
   public void testValues() {
      Assert.assertEquals(3, ReviewBlockType.values().length);
   }

   @Test
   public void testValueOf() {
      Assert.assertEquals(ReviewBlockType.Commit, ReviewBlockType.valueOf(ReviewBlockType.Commit.name()));
   }

}
