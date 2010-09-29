/*
 * Created on Sep 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.test.recordMap;

import junit.framework.Assert;
import org.eclipse.osee.ote.message.test.mock.TestMessage;
import org.junit.Test;

public class RecordMapTest {

   @Test
   public void testGetInt() {
      TestMessage msg = new TestMessage();

      // Testing below the boundary.
      try {
         msg.RECORD_MAP_1.get(1);
         Assert.assertTrue(true);
      } catch (IllegalArgumentException ex) {
         Assert.fail("We shouldn't get an exception for this get!");
      }

      // Testing on the boundary.
      try {
         msg.RECORD_MAP_1.get(2);
         Assert.fail("We should get an exception for this get on the boundary!");
      } catch (IllegalArgumentException ex) {
         Assert.assertTrue("We should get an exception for this index", true);
      }
      // Testing above the boundary.
      try {
         msg.RECORD_MAP_1.get(3);
         Assert.fail("We should get an exception for this get above the boundary!");
      } catch (IllegalArgumentException ex) {
         Assert.assertTrue("We should get an exception for this index", true);
      }

   }
}
