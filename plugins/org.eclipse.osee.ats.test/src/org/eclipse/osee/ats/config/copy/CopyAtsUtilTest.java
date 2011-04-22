/*
 * Created on Mar 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.copy;

import junit.framework.Assert;
import org.eclipse.osee.ats.config.copy.ConfigData;
import org.eclipse.osee.ats.config.copy.CopyAtsUtil;

/**
 * Test case for {@link CopyAtsUtil}
 * 
 * @author Donald G. Dunne
 */
public class CopyAtsUtilTest {

   @org.junit.Test
   public void testGetConvertedName() throws Exception {
      ConfigData data = new ConfigData();
      data.setReplaceStr("ReplStr");
      data.setSearchStr("SrchStr");

      Assert.assertEquals("ReplStr is the one", CopyAtsUtil.getConvertedName(data, "SrchStr is the one"));
   }
}
