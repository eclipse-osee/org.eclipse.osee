package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.junit.Before;

public class AccessTest {
   public AccessTest() {
   }

   @Before
   protected void setUp() throws Exception {
      assertFalse(ClientSessionManager.isProductionDataStore());
   }
}