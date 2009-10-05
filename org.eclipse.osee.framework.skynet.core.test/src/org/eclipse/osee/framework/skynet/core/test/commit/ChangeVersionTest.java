/*
 * Created on Oct 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.commit;

import org.eclipse.osee.framework.skynet.core.commit.ChangeVersion;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class ChangeVersionTest {

   @Test
   public void testConstruction() {
      ChangeVersion actual = new ChangeVersion();
      ChangeVersion expected = new ChangeVersion(null, null, null, null);
      CommitUtil.checkChange(expected, actual);
   }
}
