/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
