/*
 * Created on Jun 8, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;

/**
 * Test unit for {@link AtsTestUtil}
 * 
 * @author Donald G. Dunne
 */
public class AtsTestUtilTest extends AtsTestUtil {

   @org.junit.Test
   public void testCleanupAndReset() throws OseeCoreException {
      boolean exceptionThrown = false;
      try {
         Assert.assertNull(AtsTestUtil.getWorkDef());
      } catch (OseeStateException ex) {
         Assert.assertEquals(ex.getMessage(), "Must call cleanAndReset before using this method");
         exceptionThrown = true;
      }
      Assert.assertTrue("Exeception should have been thrown", exceptionThrown);

      AtsTestUtil.cleanupAndReset("AtsTestUtilTest");

      AtsTestUtil.validateArtifactCache();

      AtsTestUtil.cleanup();

      AtsTestUtil.validateArtifactCache();
   }

}
